package com.aiagent.repository

import com.aiagent.data.db.ChatDao
import com.aiagent.data.model.ChatMessage
import com.aiagent.network.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val apiService: ApiService,
    private val chatDao: ChatDao
) {
    // Local Database
    fun getMessagesForProject(projectId: String): Flow<List<ChatMessage>> = 
        chatDao.getMessagesForProject(projectId)
    
    fun getGeneralChatMessages(): Flow<List<ChatMessage>> = 
        chatDao.getGeneralChatMessages()
    
    suspend fun saveMessage(message: ChatMessage) = 
        chatDao.insertMessage(message)
    
    suspend fun saveMessages(messages: List<ChatMessage>) = 
        chatDao.insertMessages(messages)
    
    suspend fun deleteMessage(message: ChatMessage) = 
        chatDao.deleteMessage(message)
    
    // Remote API
    fun sendMessage(
        messages: List<ChatMessageRequest>,
        provider: String,
        apiKey: String,
        model: String?,
        baseUrl: String?,
        stream: Boolean = false
    ) = flow {
        emit(ApiResult.Loading)
        try {
            val request = ChatRequest(
                messages = messages,
                provider = provider,
                api_key = apiKey,
                model = model,
                base_url = baseUrl,
                stream = stream
            )
            
            val response = apiService.sendChatMessage(request)
            if (response.isSuccessful && response.body() != null) {
                emit(ApiResult.Success(response.body()!!))
            } else {
                emit(ApiResult.Error("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(ApiResult.Error(e.message ?: "Unknown error"))
        }
    }
    
    fun createMessage(
        content: String,
        role: String,
        projectId: String? = null,
        provider: String = ""
    ): ChatMessage {
        return ChatMessage(
            id = UUID.randomUUID().toString(),
            projectId = projectId,
            role = role,
            content = content,
            provider = provider
        )
    }
}
