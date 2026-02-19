package com.aiagent.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Agent(
    val id: String,
    val name: String,
    val nameBn: String,
    val description: String,
    val descriptionBn: String,
    val icon: String,
    val color: String,
    val capabilities: List<String>,
    val templateCount: Int = 0
) : Parcelable

object AgentData {
    val agents = listOf(
        Agent(
            id = "android",
            name = "Android Architect",
            nameBn = "অ্যান্ড্রয়েড আর্কিটেক্ট",
            description = "Creates native Android apps with Kotlin, MVVM, Jetpack Compose",
            descriptionBn = "Kotlin, MVVM, Jetpack Compose দিয়ে অ্যান্ড্রয়েড অ্যাপ বানায়",
            icon = "📱",
            color = "#3DDC84",
            capabilities = listOf("Kotlin", "XML Layout", "Jetpack", "Room DB", "Retrofit"),
            templateCount = 15
        ),
        Agent(
            id = "python",
            name = "Python Backend",
            nameBn = "পাইথন ব্যাকএন্ড",
            description = "Builds FastAPI/Flask APIs with SQLAlchemy and authentication",
            descriptionBn = "FastAPI/Flask API, SQLAlchemy, অথেন্টিকেশন সহ ব্যাকএন্ড বানায়",
            icon = "🐍",
            color = "#3776AB",
            capabilities = listOf("FastAPI", "Flask", "SQLAlchemy", "JWT", "Docker"),
            templateCount = 12
        ),
        Agent(
            id = "uiux",
            name = "UI/UX Designer",
            nameBn = "UI/UX ডিজাইনার",
            description = "Creates beautiful Material Design 3 interfaces and animations",
            descriptionBn = "সুন্দর Material Design 3 ইন্টারফেস এবং অ্যানিমেশন তৈরি করে",
            icon = "🎨",
            color = "#FF6B6B",
            capabilities = listOf("Material 3", "Animations", "Custom Views", "Themes"),
            templateCount = 8
        ),
        Agent(
            id = "fullstack",
            name = "Full Stack",
            nameBn = "ফুল স্ট্যাক",
            description = "Android + Python backend with complete integration",
            descriptionBn = "অ্যান্ড্রয়েড + পাইথন ব্যাকএন্ড সম্পূর্ণ ইন্টিগ্রেশন সহ",
            icon = "🔥",
            color = "#FF9800",
            capabilities = listOf("Android", "FastAPI", "REST API", "WebSocket"),
            templateCount = 10
        ),
        Agent(
            id = "ml",
            name = "ML Engineer",
            nameBn = "ML ইঞ্জিনিয়ার",
            description = "Machine learning models and AI integration",
            descriptionBn = "মেশিন লার্নিং মডেল এবং AI ইন্টিগ্রেশন",
            icon = "🤖",
            color = "#9C27B0",
            capabilities = listOf("TensorFlow", "PyTorch", "ML Kit", "OpenCV"),
            templateCount = 6
        )
    )
    
    fun getById(id: String): Agent? = agents.find { it.id == id }
}
