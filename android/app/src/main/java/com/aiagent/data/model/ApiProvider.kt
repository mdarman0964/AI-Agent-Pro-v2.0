package com.aiagent.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ApiProvider(
    val id: String,
    val name: String,
    val nameBn: String,
    val baseUrl: String,
    val defaultModel: String,
    val requiresKey: Boolean = true,
    val isOpenAICompatible: Boolean = true,
    val description: String = ""
) : Parcelable

object ApiProviders {
    val all = listOf(
        ApiProvider(
            id = "moonshot",
            name = "Moonshot (Kimi)",
            nameBn = "মুনশট (কিমি)",
            baseUrl = "https://api.moonshot.cn/v1/",
            defaultModel = "kimi-2.5",
            description = "Fast and affordable Chinese LLM"
        ),
        ApiProvider(
            id = "openai",
            name = "OpenAI (GPT-4)",
            nameBn = "ওপেনএআই (জিপিটি-৪)",
            baseUrl = "https://api.openai.com/v1/",
            defaultModel = "gpt-4-turbo-preview",
            description = "Most capable GPT model"
        ),
        ApiProvider(
            id = "anthropic",
            name = "Anthropic (Claude)",
            nameBn = "অ্যানথ্রোপিক (ক্লড)",
            baseUrl = "https://api.anthropic.com/",
            defaultModel = "claude-3-opus-20240229",
            isOpenAICompatible = false,
            description = "Excellent for long context"
        ),
        ApiProvider(
            id = "deepseek",
            name = "DeepSeek",
            nameBn = "ডিপসিক",
            baseUrl = "https://api.deepseek.com/v1/",
            defaultModel = "deepseek-chat",
            description = "Cost-effective coding model"
        ),
        ApiProvider(
            id = "groq",
            name = "Groq",
            nameBn = "গ্রোক",
            baseUrl = "https://api.groq.com/openai/v1/",
            defaultModel = "llama2-70b-4096",
            description = "Ultra-fast inference"
        ),
        ApiProvider(
            id = "gemini",
            name = "Google Gemini",
            nameBn = "গুগল জেমিনি",
            baseUrl = "https://generativelanguage.googleapis.com/v1beta/",
            defaultModel = "gemini-pro",
            isOpenAICompatible = false,
            description = "Google's multimodal AI"
        ),
        ApiProvider(
            id = "custom",
            name = "Custom / Local LLM",
            nameBn = "কাস্টম / লোকাল LLM",
            baseUrl = "http://localhost:11434/v1/",
            defaultModel = "llama2",
            description = "Your own LLM server (Ollama, etc.)"
        )
    )
    
    fun getById(id: String): ApiProvider? = all.find { it.id == id }
}
