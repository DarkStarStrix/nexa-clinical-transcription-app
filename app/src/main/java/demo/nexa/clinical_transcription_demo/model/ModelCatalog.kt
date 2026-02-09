package demo.nexa.clinical_transcription_demo.model

/**
 * Central model catalog for bounty mode.
 *
 * Sources are ordered; downloader should try each URL until one works.
 *
 * Note: These URLs intentionally point at public hosting. If you have a preferred
 * Nexa-hosted bucket for models, swap URLs here.
 */
object ModelCatalog {

    // Hugging Face "resolve" URLs (no auth assumed for public repos).
    private fun hfResolve(repo: String, path: String): String {
        return "https://huggingface.co/$repo/resolve/main/$path"
    }

    val asrParakeetNexa = ModelSpec(
        id = "asr_parakeet_nexa",
        displayName = "ASR: Parakeet (NPU)",
        kind = ModelSpec.Kind.ASR,
        installRelPath = "nexa_models/parakeet-tdt-0.6b-v3-npu-mobile",
        files = listOf(
            ModelFile(
                fileName = "files-1-2.nexa",
                urls = listOf(hfResolve("NexaAI/parakeet-tdt-0.6b-v3-npu-mobile", "files-1-2.nexa")),
                minBytes = 100
            ),
            ModelFile(
                fileName = "files-2-2.nexa",
                urls = listOf(hfResolve("NexaAI/parakeet-tdt-0.6b-v3-npu-mobile", "files-2-2.nexa")),
                minBytes = 1000
            ),
            ModelFile(
                fileName = "weights-1-5.nexa",
                urls = listOf(hfResolve("NexaAI/parakeet-tdt-0.6b-v3-npu-mobile", "weights-1-5.nexa")),
                minBytes = 10_000_000
            ),
            ModelFile(
                fileName = "weights-2-5.nexa",
                urls = listOf(hfResolve("NexaAI/parakeet-tdt-0.6b-v3-npu-mobile", "weights-2-5.nexa")),
                minBytes = 10_000_000
            ),
            ModelFile(
                fileName = "weights-3-5.nexa",
                urls = listOf(hfResolve("NexaAI/parakeet-tdt-0.6b-v3-npu-mobile", "weights-3-5.nexa")),
                minBytes = 10_000_000
            ),
            ModelFile(
                fileName = "weights-4-5.nexa",
                urls = listOf(hfResolve("NexaAI/parakeet-tdt-0.6b-v3-npu-mobile", "weights-4-5.nexa")),
                minBytes = 10_000_000
            ),
            ModelFile(
                fileName = "weights-5-5.nexa",
                urls = listOf(hfResolve("NexaAI/parakeet-tdt-0.6b-v3-npu-mobile", "weights-5-5.nexa")),
                minBytes = 100_000
            )
        )
    )

    val llmLiquidStarter = ModelSpec(
        id = "llm_liquid_starter",
        displayName = "LLM: Liquid (Starter SOAP)",
        kind = ModelSpec.Kind.LLM,
        installRelPath = "nexa_models/LFM2.5-1.2B-Instruct-GGUF",
        files = listOf(
            ModelFile(
                fileName = "LFM2.5-1.2B-Instruct-Q4_K_M.gguf",
                urls = listOf(
                    hfResolve("LiquidAI/LFM2.5-1.2B-Instruct-GGUF", "LFM2.5-1.2B-Instruct-Q4_K_M.gguf")
                ),
                // GGUF pointer vs real file: ensure it's not just a tiny placeholder.
                minBytes = 50_000_000
            )
        )
    )

    val llmQwenUpgrade = ModelSpec(
        id = "llm_qwen_upgrade",
        displayName = "LLM: Qwen3 4B (Upgrade SOAP)",
        kind = ModelSpec.Kind.LLM,
        installRelPath = "nexa_models/Qwen3-4B-GGUF",
        files = listOf(
            ModelFile(
                fileName = "Qwen3-4B-Q4_K_M.gguf",
                urls = listOf(
                    hfResolve("Qwen/Qwen3-4B-GGUF", "Qwen3-4B-Q4_K_M.gguf")
                ),
                minBytes = 100_000_000
            )
        )
    )

    val all: List<ModelSpec> = listOf(
        asrParakeetNexa,
        llmLiquidStarter,
        llmQwenUpgrade
    )
}

