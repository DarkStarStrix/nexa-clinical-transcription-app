package demo.nexa.clinical_transcription_demo.model

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.TimeUnit

enum class ModelInstallStatus { INSTALLED, PARTIAL, MISSING }

data class ModelFileStatus(
    val fileName: String,
    val exists: Boolean,
    val bytes: Long,
    val meetsMinBytes: Boolean
)

sealed class DownloadEvent {
    data class FileProgress(
        val modelId: String,
        val fileName: String,
        val bytesDownloaded: Long,
        val totalBytes: Long?
    ) : DownloadEvent()
    data class FileCompleted(val modelId: String, val fileName: String) : DownloadEvent()
}

class ModelDownloader(
    private val context: Context,
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()
) {

    private fun modelsRootDir(): File {
        return File(context.filesDir, "nexa_models").apply { mkdirs() }
    }

    fun getInstallDir(spec: ModelSpec): File {
        // installRelPath always starts with "nexa_models/..."
        val rel = spec.installRelPath.removePrefix("nexa_models/").removePrefix("/")
        return File(modelsRootDir(), rel)
    }

    fun isInstalled(spec: ModelSpec): Boolean {
        val dir = getInstallDir(spec)
        if (!dir.exists() || !dir.isDirectory) return false
        return spec.files.all { f ->
            val file = File(dir, f.fileName)
            file.exists() && file.isFile && (f.minBytes == null || file.length() >= f.minBytes)
        }
    }

    fun getInstallStatus(spec: ModelSpec): ModelInstallStatus {
        if (isInstalled(spec)) return ModelInstallStatus.INSTALLED

        val dir = getInstallDir(spec)
        if (!dir.exists() || !dir.isDirectory) return ModelInstallStatus.MISSING

        val hasAny =
            spec.files.any { f -> File(dir, f.fileName).exists() } ||
                spec.files.any { f -> File(dir, "${f.fileName}.partial").exists() }

        return if (hasAny) ModelInstallStatus.PARTIAL else ModelInstallStatus.MISSING
    }

    fun getFileStatuses(spec: ModelSpec): List<ModelFileStatus> {
        val dir = getInstallDir(spec)
        return spec.files.map { f ->
            val file = File(dir, f.fileName)
            val bytes = if (file.exists() && file.isFile) file.length() else 0L
            val meets = f.minBytes?.let { bytes >= it } ?: (file.exists() && file.isFile)
            ModelFileStatus(
                fileName = f.fileName,
                exists = file.exists() && file.isFile,
                bytes = bytes,
                meetsMinBytes = meets
            )
        }
    }

    suspend fun delete(spec: ModelSpec): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val dir = getInstallDir(spec)
            if (dir.exists()) dir.deleteRecursively()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Download all files for a spec into filesDir under the install path.
     * Writes to *.partial and atomically renames on success.
     */
    suspend fun download(
        spec: ModelSpec,
        onEvent: (DownloadEvent) -> Unit
    ): Result<Unit> = withContext(Dispatchers.IO) {
        val dir = getInstallDir(spec).apply { mkdirs() }

        try {
            for (mf in spec.files) {
                val target = File(dir, mf.fileName)
                val partial = File(dir, "${mf.fileName}.partial")

                if (target.exists() && (mf.minBytes == null || target.length() >= mf.minBytes)) {
                    continue
                }

                val existing = if (partial.exists()) partial.length() else 0L
                val result = downloadOneFile(
                    modelId = spec.id,
                    mf = mf,
                    partial = partial,
                    existingBytes = existing,
                    onEvent = onEvent
                )

                result.getOrElse { return@withContext Result.failure(it) }

                if (target.exists()) target.delete()
                if (!partial.renameTo(target)) {
                    return@withContext Result.failure(IOException("Failed to finalize ${mf.fileName}"))
                }
                onEvent(DownloadEvent.FileCompleted(spec.id, mf.fileName))
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun downloadOneFile(
        modelId: String,
        mf: ModelFile,
        partial: File,
        existingBytes: Long,
        onEvent: (DownloadEvent) -> Unit
    ): Result<Unit> {
        var lastError: Throwable? = null

        for (url in mf.urls) {
            try {
                val requestBuilder = Request.Builder()
                    .url(url)
                    .header("User-Agent", "clinical-transcription-demo/1.0 (bounty)")

                // Try resume when we have a partial download.
                if (existingBytes > 0) {
                    requestBuilder.header("Range", "bytes=$existingBytes-")
                }

                val response = client.newCall(requestBuilder.build()).execute()
                if (!response.isSuccessful) {
                    response.close()
                    throw IOException("HTTP ${response.code} downloading ${mf.fileName}")
                }

                val body = response.body ?: throw IOException("Empty response body for ${mf.fileName}")

                val totalBytes = body.contentLength().let { if (it > 0) it else null }

                // If server ignored Range and returned full body, reset partial.
                val append = existingBytes > 0 && response.code == 206
                if (!append && existingBytes > 0) {
                    partial.delete()
                }

                body.byteStream().use { input ->
                    FileOutputStream(partial, append).use { output ->
                        val buf = ByteArray(1024 * 256)
                        var downloaded = if (append) existingBytes else 0L
                        while (true) {
                            val read = input.read(buf)
                            if (read <= 0) break
                            output.write(buf, 0, read)
                            downloaded += read
                            onEvent(
                                DownloadEvent.FileProgress(
                                    modelId = modelId,
                                    fileName = mf.fileName,
                                    bytesDownloaded = downloaded,
                                    totalBytes = totalBytes?.let { if (append) it + existingBytes else it }
                                )
                            )
                        }
                        output.flush()
                    }
                }

                response.close()

                val minBytes = mf.minBytes
                if (minBytes != null && partial.length() < minBytes) {
                    throw IOException("Downloaded ${mf.fileName} is too small (${partial.length()} bytes)")
                }

                return Result.success(Unit)
            } catch (e: Exception) {
                lastError = e
            }
        }

        return Result.failure(lastError ?: IOException("Failed to download ${mf.fileName}"))
    }
}
