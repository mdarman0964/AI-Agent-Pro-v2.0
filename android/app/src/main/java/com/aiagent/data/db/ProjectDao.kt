package com.aiagent.data.db

import androidx.room.*
import com.aiagent.data.model.GeneratedProject
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    
    @Query("SELECT * FROM generated_projects ORDER BY createdAt DESC")
    fun getAllProjects(): Flow<List<GeneratedProject>>
    
    @Query("SELECT * FROM generated_projects WHERE id = :projectId")
    suspend fun getProjectById(projectId: String): GeneratedProject?
    
    @Query("SELECT * FROM generated_projects WHERE agentType = :agentType ORDER BY createdAt DESC")
    fun getProjectsByAgentType(agentType: String): Flow<List<GeneratedProject>>
    
    @Query("SELECT * FROM generated_projects WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavoriteProjects(): Flow<List<GeneratedProject>>
    
    @Query("SELECT * FROM generated_projects WHERE name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    fun searchProjects(query: String): Flow<List<GeneratedProject>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: GeneratedProject)
    
    @Update
    suspend fun updateProject(project: GeneratedProject)
    
    @Delete
    suspend fun deleteProject(project: GeneratedProject)
    
    @Query("DELETE FROM generated_projects WHERE id = :projectId")
    suspend fun deleteProjectById(projectId: String)
    
    @Query("UPDATE generated_projects SET isFavorite = :isFavorite WHERE id = :projectId")
    suspend fun updateFavoriteStatus(projectId: String, isFavorite: Boolean)
    
    @Query("SELECT COUNT(*) FROM generated_projects")
    suspend fun getProjectCount(): Int
    
    @Query("DELETE FROM generated_projects")
    suspend fun deleteAllProjects()
}
