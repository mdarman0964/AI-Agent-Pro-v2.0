package com.aiagent.ui.generate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiagent.data.model.GeneratedFile
import com.aiagent.network.ApiResult
import com.aiagent.repository.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GitHubPushViewModel @Inject constructor(
    private val projectRepository: ProjectRepository
) : ViewModel() {
    
    private val _pushState = MutableStateFlow<GitHubPushState>(GitHubPushState.Idle)
    val pushState: StateFlow<GitHubPushState> = _pushState
    
    fun pushToGitHub(
        token: String,
        repoName: String,
        files: List<GeneratedFile>,
        branch: String = "main"
    ) {
        viewModelScope.launch {
            _pushState.value = GitHubPushState.Loading
            
            projectRepository.pushToGitHub(token, repoName, files, branch)
                .collectLatest { result ->
                    when (result) {
                        is ApiResult.Loading -> {
                            _pushState.value = GitHubPushState.Loading
                        }
                        is ApiResult.Success -> {
                            _pushState.value = GitHubPushState.Success(result.data.repo_url)
                        }
                        is ApiResult.Error -> {
                            _pushState.value = GitHubPushState.Error(result.message)
                        }
                    }
                }
        }
    }
}

sealed class GitHubPushState {
    object Idle : GitHubPushState()
    object Loading : GitHubPushState()
    data class Success(val repoUrl: String) : GitHubPushState()
    data class Error(val message: String) : GitHubPushState()
}
