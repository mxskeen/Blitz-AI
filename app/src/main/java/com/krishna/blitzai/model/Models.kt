package com.krishna.blitzai.model

object Models {
    // Chat-capable models from Chutes AI
    // See: https://chutes.ai for full list
    val chatModels: List<String> = listOf(
        // Popular Models
        "chutesai/Mistral-Small-24B-Instruct-2501",
        "chutesai/Llama-3.3-70B-Instruct",
        "chutesai/Llama-3.1-8B-Instruct",
        "chutesai/DeepSeek-R1-Distill-Qwen-32B",
        "chutesai/Llama-3.2-3B-Instruct",
        "chutesai/Qwen2.5-72B-Instruct",
        "chutesai/Qwen2.5-Coder-32B-Instruct",
        "chutesai/Nous-DeepHermes-3-Llama-3-8B-Preview",
        "chutesai/Mistral-Nemo-Instruct-2407",
        "chutesai/phi-4",
        // Reasoning Models
        "chutesai/DeepSeek-R1-Distill-Llama-70B",
        "chutesai/QwQ-32B-Preview",
        // Vision Models
        "chutesai/Qwen2-VL-72B-Instruct",
        "chutesai/Llama-3.2-11B-Vision-Instruct"
    )

    // Speech models (when supported by Chutes)
    val speechModels: List<String> = listOf(
        "openai/whisper-large-v3"
    )
}
