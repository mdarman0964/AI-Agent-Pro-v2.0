package com.aiagent.ui.generate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiagent.data.model.Agent
import com.aiagent.data.model.GeneratedFile
import com.aiagent.data.model.GeneratedProject
import com.aiagent.data.model.ProjectRequest
import com.aiagent.network.ApiResult
import com.aiagent.network.ProjectResponse
import com.aiagent.repository.ChatRepository
import com.aiagent.repository.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class GenerateViewModel @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val chatRepository: ChatRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<GenerateUiState>(GenerateUiState.Idle)
    val uiState: StateFlow<GenerateUiState> = _uiState
    
    private var currentProject: GeneratedProject? = null
    
    fun generateProject(
        agent: Agent,
        projectName: String,
        description: String,
        features: List<String>
    ) {
        viewModelScope.launch {
            _uiState.value = GenerateUiState.Loading
            
            // Get API config from DataStore (simplified - should be injected)
            val apiKey = "your-api-key" // Get from preferences
            val provider = "openai"
            val model: String? = null
            val baseUrl: String? = null
            
            projectRepository.generateProject(
                agentType = agent.id,
                projectName = projectName,
                description = description,
                features = features,
                provider = provider,
                model = model,
                apiKey = apiKey,
                baseUrl = baseUrl
            ).collectLatest { result ->
                when (result) {
                    is ApiResult.Loading -> {
                        _uiState.value = GenerateUiState.Loading
                    }
                    is ApiResult.Success -> {
                        _uiState.value = GenerateUiState.Success(result.data)
                        // Create project object for saving
                        currentProject = GeneratedProject(
                            id = UUID.randomUUID().toString(),
                            name = projectName,
                            agentType = agent.id,
                            description = description,
                            provider = provider,
                            model = model ?: "default",
                            files = result.data.files,
                            projectStructure = result.data.projectStructure
                        )
                    }
                    is ApiResult.Error -> {
                        _uiState.value = GenerateUiState.Error(result.message)
                    }
                }
            }
        }
    }
    
    fun saveToHistory(
        projectName: String,
        description: String,
        files: List<GeneratedFile>
    ) {
        viewModelScope.launch {
            val project = GeneratedProject(
                id = UUID.randomUUID().toString(),
                name = projectName,
                agentType = currentProject?.agentType ?: "android",
                description = description,
                provider = currentProject?.provider ?: "openai",
                model = currentProject?.model ?: "default",
                files = files,
                projectStructure = currentProject?.projectStructure ?: ""
            )
            projectRepository.saveProject(project)
        }
    }
}

sealed class GenerateUiState {
    object Idle : GenerateUiState()
    object Loading : GenerateUiState()
    data class Success(val response: ProjectResponse) : GenerateUiState()
    data class Error(val message: String) : GenerateUiState()
}
