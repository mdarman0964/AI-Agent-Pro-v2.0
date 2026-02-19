package com.aiagent.ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiagent.data.model.ChatMessage
import com.aiagent.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {
    
    private val _messages = MutableLiveData<List<ChatMessage>>(emptyList())
    val messages: LiveData<List<ChatMessage>> = _messages
    
    private val currentMessages = mutableListOf<ChatMessage>()
    
    fun sendMessage(content: String) {
        // Add user message
        val userMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            projectId = null,
            role = "user",
            content = content
        )
        currentMessages.add(userMessage)
        _messages.value = currentMessages.toList()
        
        // Send to AI
        viewModelScope.launch {
            val requestMessages = currentMessages.map {
                ChatMessageRequest(role = it.role, content = it.content)
            }
            
            chatRepository.sendMessage(
                messages = requestMessages,
                provider = "openai", // Get from preferences
                apiKey = "your-api-key", // Get from preferences
                model = null,
                baseUrl = null
            ).collect { result ->
                when (result) {
                    is com.aiagent.network.ApiResult.Success -> {
                        val aiMessage = ChatMessage(
                            id = UUID.randomUUID().toString(),
                            projectId = null,
                            role = "assistant",
                            content = result.data.response
                        )
                        currentMessages.add(aiMessage)
                        _messages.value = currentMessages.toList()
                        
                        // Save to database
                        chatRepository.saveMessage(userMessage)
                        chatRepository.saveMessage(aiMessage)
                    }
                    is com.aiagent.network.ApiResult.Error -> {
                        // Show error
                    }
                    else -> {}
                }
            }
        }
    }
}

data class ChatMessageRequest(
    val role: String,
    val content: String
)
