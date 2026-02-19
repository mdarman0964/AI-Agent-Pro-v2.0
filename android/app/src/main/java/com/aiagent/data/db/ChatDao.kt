package com.aiagent.data.db

import androidx.room.*
import com.aiagent.data.model.ChatMessage
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    
    @Query("SELECT * FROM chat_history WHERE projectId = :projectId ORDER BY timestamp ASC")
    fun getMessagesForProject(projectId: String): Flow<List<ChatMessage>>
    
    @Query("SELECT * FROM chat_history WHERE projectId IS NULL ORDER BY timestamp ASC")
    fun getGeneralChatMessages(): Flow<List<ChatMessage>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<ChatMessage>)
    
    @Delete
    suspend fun deleteMessage(message: ChatMessage)
    
    @Query("DELETE FROM chat_history WHERE projectId = :projectId")
    suspend fun deleteMessagesForProject(projectId: String)
    
    @Query("DELETE FROM chat_history")
    suspend fun deleteAllMessages()
    
    @Query("SELECT COUNT(*) FROM chat_history WHERE projectId = :projectId")
    suspend fun getMessageCountForProject(projectId: String): Int
}
