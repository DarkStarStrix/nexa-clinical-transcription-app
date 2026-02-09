package demo.nexa.clinical_transcription_demo.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import demo.nexa.clinical_transcription_demo.model.DownloadEvent
import demo.nexa.clinical_transcription_demo.model.ModelCatalog
import demo.nexa.clinical_transcription_demo.model.ModelDownloader
import demo.nexa.clinical_transcription_demo.model.ModelFileStatus
import demo.nexa.clinical_transcription_demo.model.ModelInstallStatus
import demo.nexa.clinical_transcription_demo.model.ModelSpec
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ModelRowState(
    val spec: ModelSpec,
    val status: ModelInstallStatus,
    val isDownloading: Boolean,
    val expanded: Boolean,
    val fileStatuses: List<ModelFileStatus> = emptyList(),
    val currentFileName: String? = null,
    val currentFilePct: Int? = null,
    val overallPct: Int? = null,
    val progressText: String? = null,
    val errorText: String? = null
)

data class ModelCenterState(
    val rows: List<ModelRowState> = emptyList(),
    val overallBusy: Boolean = false
)

class ModelCenterViewModel(application: Application) : AndroidViewModel(application) {

    private val downloader = ModelDownloader(application.applicationContext)

    private val _state = MutableStateFlow(ModelCenterState())
    val state: StateFlow<ModelCenterState> = _state.asStateFlow()

    private var currentJob: Job? = null

    init {
        refresh()
    }

    fun refresh() {
        val rows = ModelCatalog.all.map { spec ->
            ModelRowState(
                spec = spec,
                status = downloader.getInstallStatus(spec),
                isDownloading = false,
                expanded = false,
                fileStatuses = downloader.getFileStatuses(spec)
            )
        }
        _state.value = ModelCenterState(rows = rows, overallBusy = false)
    }

    fun toggleExpanded(specId: String) {
        _state.update { current ->
            current.copy(
                rows = current.rows.map { row ->
                    if (row.spec.id == specId) row.copy(expanded = !row.expanded) else row
                }
            )
        }
    }

    fun installRequired() {
        // Run sequentially to keep UX predictable and avoid bandwidth spikes.
        // Required = ASR + Liquid starter.
        val required = listOf(ModelCatalog.asrParakeetNexa, ModelCatalog.llmLiquidStarter)
        if (currentJob?.isActive == true) return

        currentJob = viewModelScope.launch {
            _state.update { it.copy(overallBusy = true) }
            for (spec in required) {
                val specId = spec.id
                setRow(specId) { it.copy(isDownloading = true, errorText = null, progressText = "Starting…") }

                val result = downloader.download(spec) { event ->
                    when (event) {
                        is DownloadEvent.FileProgress -> {
                            val total = event.totalBytes
                            val pct = if (total != null && total > 0) {
                                ((event.bytesDownloaded.toDouble() / total.toDouble()) * 100.0).toInt().coerceIn(0, 100)
                            } else null

                            setRow(specId) {
                                it.copy(
                                    currentFileName = event.fileName,
                                    currentFilePct = pct,
                                    progressText = if (pct != null) "Downloading ${event.fileName} ($pct%)"
                                    else "Downloading ${event.fileName} (${event.bytesDownloaded / (1024 * 1024)}MB)"
                                )
                            }
                        }
                        is DownloadEvent.FileCompleted -> {
                            setRow(specId) { it.copy(progressText = "Downloaded ${event.fileName}") }
                        }
                    }
                }

                if (result.isFailure) {
                    val e = result.exceptionOrNull()
                    setRow(specId) {
                        it.copy(
                            status = downloader.getInstallStatus(spec),
                            isDownloading = false,
                            currentFileName = null,
                            currentFilePct = null,
                            progressText = null,
                            errorText = e?.message ?: "Download failed",
                            fileStatuses = downloader.getFileStatuses(spec)
                        )
                    }
                    _state.update { it.copy(overallBusy = false) }
                    return@launch
                }

                setRow(specId) {
                    it.copy(
                        status = ModelInstallStatus.INSTALLED,
                        isDownloading = false,
                        currentFileName = null,
                        currentFilePct = null,
                        overallPct = 100,
                        progressText = "Installed",
                        errorText = null,
                        fileStatuses = downloader.getFileStatuses(spec)
                    )
                }
            }
            _state.update { it.copy(overallBusy = false) }
        }
    }

    fun download(specId: String) {
        val spec = ModelCatalog.all.firstOrNull { it.id == specId } ?: return
        if (currentJob?.isActive == true) return

        currentJob = viewModelScope.launch {
            _state.update { it.copy(overallBusy = true) }
            setRow(specId) { it.copy(isDownloading = true, errorText = null, progressText = "Starting…", overallPct = null) }

            val result = downloader.download(spec) { event ->
                when (event) {
                    is DownloadEvent.FileProgress -> {
                        val total = event.totalBytes
                        val pct = if (total != null && total > 0) {
                            ((event.bytesDownloaded.toDouble() / total.toDouble()) * 100.0).toInt().coerceIn(0, 100)
                        } else null
                        setRow(specId) {
                            it.copy(
                                currentFileName = event.fileName,
                                currentFilePct = pct,
                                progressText = if (pct != null) "Downloading ${event.fileName} ($pct%)"
                                else "Downloading ${event.fileName} (${event.bytesDownloaded / (1024 * 1024)}MB)"
                            )
                        }
                    }
                    is DownloadEvent.FileCompleted -> {
                        setRow(specId) { it.copy(progressText = "Downloaded ${event.fileName}") }
                    }
                }
            }

            result.onSuccess {
                setRow(specId) {
                    it.copy(
                        status = ModelInstallStatus.INSTALLED,
                        isDownloading = false,
                        currentFileName = null,
                        currentFilePct = null,
                        overallPct = 100,
                        progressText = "Installed",
                        errorText = null,
                        fileStatuses = downloader.getFileStatuses(spec)
                    )
                }
            }.onFailure { e ->
                setRow(specId) {
                    it.copy(
                        status = downloader.getInstallStatus(spec),
                        isDownloading = false,
                        currentFileName = null,
                        currentFilePct = null,
                        overallPct = null,
                        progressText = null,
                        errorText = e.message ?: "Download failed",
                        fileStatuses = downloader.getFileStatuses(spec)
                    )
                }
            }

            _state.update { it.copy(overallBusy = false) }
        }
    }

    fun delete(specId: String) {
        val spec = ModelCatalog.all.firstOrNull { it.id == specId } ?: return
        if (currentJob?.isActive == true) return

        currentJob = viewModelScope.launch {
            _state.update { it.copy(overallBusy = true) }
            setRow(specId) { it.copy(isDownloading = true, errorText = null, progressText = "Deleting…") }
            val result = downloader.delete(spec)
            result.onSuccess {
                setRow(specId) {
                    it.copy(
                        status = ModelInstallStatus.MISSING,
                        isDownloading = false,
                        currentFileName = null,
                        currentFilePct = null,
                        overallPct = null,
                        progressText = null,
                        errorText = null,
                        fileStatuses = downloader.getFileStatuses(spec)
                    )
                }
            }.onFailure { e ->
                setRow(specId) {
                    it.copy(isDownloading = false, progressText = null, errorText = e.message ?: "Delete failed")
                }
            }

            _state.update { it.copy(overallBusy = false) }
        }
    }

    private fun setRow(specId: String, block: (ModelRowState) -> ModelRowState) {
        _state.update { current ->
            current.copy(
                rows = current.rows.map { row ->
                    if (row.spec.id == specId) block(row) else row
                }
            )
        }
    }
}
