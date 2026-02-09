package demo.nexa.clinical_transcription_demo.model

/**
 * A single downloadable model artifact.
 * - For GGUF: a single file.
 * - For NEXA folder models: multiple files sharing a common install dir.
 */
data class ModelSpec(
    val id: String,
    val displayName: String,
    val kind: Kind,
    val installRelPath: String,
    val files: List<ModelFile>
) {
    enum class Kind { ASR, LLM }
}

data class ModelFile(
    val fileName: String,
    val urls: List<String>,
    val minBytes: Long? = null
)

