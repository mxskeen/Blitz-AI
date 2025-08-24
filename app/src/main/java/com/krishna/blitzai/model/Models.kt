package com.krishna.blitzai.model

object Models {
    // Chat-capable models and other available options from Groq docs
    // For now we include major chat/text models and whisper TTS for completeness
    // You can filter further in UI if needed.
    val chatModels: List<String> = listOf(
        // Stable
        "llama-3.1-8b-instant",
        "llama-3.3-70b-versatile",
        "meta-llama/llama-guard-4-12b",
        "openai/gpt-oss-120b",
        "openai/gpt-oss-20b",
        // Preview
        "deepseek-r1-distill-llama-70b",
        "meta-llama/llama-4-maverick-17b-128e-instruct",
        "meta-llama/llama-4-scout-17b-16e-instruct",
        "meta-llama/llama-prompt-guard-2-22m",
        "meta-llama/llama-prompt-guard-2-86m",
        "moonshotai/kimi-k2-instruct",
        "qwen/qwen3-32b"
    )

    // Speech models (not for chat completion but useful elsewhere)
    val speechModels: List<String> = listOf(
        "whisper-large-v3",
        "whisper-large-v3-turbo"
    )
}
