package com.aiagent.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.aiagent.data.db.Converters
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
@Entity(tableName = "generated_projects")
@TypeConverters(Converters::class)
data class GeneratedProject(
    @PrimaryKey
    val id: String,
    val name: String,
    val agentType: String,
    val description: String,
    val provider: String,
    val model: String,
    val files: List<GeneratedFile>,
    val projectStructure: String,
    val createdAt: Date = Date(),
    val isFavorite: Boolean = false,
    val tags: List<String> = emptyList()
) : Parcelable

@Parcelize
data class GeneratedFile(
    val path: String,
    val content: String,
    val language: String,
    val size: Int = 0
) : Parcelable

@Parcelize
@Entity(tableName = "chat_history")
data class ChatMessage(
    @PrimaryKey
    val id: String,
    val projectId: String?,
    val role: String, // "user" or "assistant"
    val content: String,
    val timestamp: Date = Date(),
    val provider: String = ""
) : Parcelable

@Parcelize
data class ProjectTemplate(
    val id: String,
    val name: String,
    val nameBn: String,
    val agentType: String,
    val description: String,
    val defaultFeatures: List<String>,
    val icon: String
) : Parcelable

object ProjectTemplates {
    val templates = listOf(
        ProjectTemplate(
            id = "ecommerce",
            name = "E-Commerce App",
            nameBn = "ই-কমার্স অ্যাপ",
            agentType = "android",
            description = "Online shopping app with cart, payment, and order tracking",
            defaultFeatures = listOf("Product catalog", "Shopping cart", "Payment integration", "Order tracking", "User profiles"),
            icon = "🛒"
        ),
        ProjectTemplate(
            id = "notes",
            name = "Notes App",
            nameBn = "নোটস অ্যাপ",
            agentType = "android",
            description = "Note taking app with categories, search, and reminders",
            defaultFeatures = listOf("Create/edit notes", "Categories", "Search", "Reminders", "Cloud sync"),
            icon = "📝"
        ),
        ProjectTemplate(
            id = "chat",
            name = "Chat App",
            nameBn = "চ্যাট অ্যাপ",
            agentType = "fullstack",
            description = "Real-time messaging app with groups and media sharing",
            defaultFeatures = listOf("Real-time chat", "Group messages", "Media sharing", "Push notifications", "Read receipts"),
            icon = "💬"
        ),
        ProjectTemplate(
            id = "health",
            name = "Health Tracker",
            nameBn = "হেলথ ট্র্যাকার",
            agentType = "android",
            description = "Health and fitness tracking app",
            defaultFeatures = listOf("Activity tracking", "Heart rate", "Sleep analysis", "Calories", "Workout plans"),
            icon = "❤️"
        ),
        ProjectTemplate(
            id = "education",
            name = "Education Platform",
            nameBn = "শিক্ষা প্ল্যাটফর্ম",
            agentType = "fullstack",
            description = "Online learning platform with courses and quizzes",
            defaultFeatures = listOf("Video courses", "Quizzes", "Progress tracking", "Certificates", "Discussion forum"),
            icon = "📚"
        ),
        ProjectTemplate(
            id = "weather",
            name = "Weather App",
            nameBn = "আবহাওয়া অ্যাপ",
            agentType = "android",
            description = "Weather forecast app with maps and alerts",
            defaultFeatures = listOf("Current weather", "Forecast", "Maps", "Alerts", "Multiple locations"),
            icon = "🌤️"
        ),
        ProjectTemplate(
            id = "todo",
            name = "Todo Manager",
            nameBn = "টুডু ম্যানেজার",
            agentType = "android",
            description = "Task management with priorities and deadlines",
            defaultFeatures = listOf("Create tasks", "Priorities", "Due dates", "Categories", "Subtasks"),
            icon = "✅"
        ),
        ProjectTemplate(
            id = "restaurant",
            name = "Food Delivery",
            nameBn = "ফুড ডেলিভারি",
            agentType = "fullstack",
            description = "Food ordering and delivery app",
            defaultFeatures = listOf("Restaurant listings", "Menu", "Cart", "Order tracking", "Payment"),
            icon = "🍔"
        )
    )
    
    fun getById(id: String): ProjectTemplate? = templates.find { it.id == id }
    fun getByAgentType(agentType: String): List<ProjectTemplate> = 
        templates.filter { it.agentType == agentType || it.agentType == "fullstack" }
}
