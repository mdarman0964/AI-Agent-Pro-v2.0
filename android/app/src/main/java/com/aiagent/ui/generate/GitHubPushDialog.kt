package com.aiagent.ui.generate

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.aiagent.data.model.GeneratedFile
import com.aiagent.databinding.DialogGithubPushBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GitHubPushDialog : DialogFragment() {
    
    private var _binding: DialogGithubPushBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GitHubPushViewModel by viewModels()
    
    private lateinit var files: List<GeneratedFile>
    
    companion object {
        private const val ARG_FILES = "files"
        
        fun newInstance(files: List<GeneratedFile>): GitHubPushDialog {
            return GitHubPushDialog().apply {
                arguments = bundleOf(
                    ARG_FILES to ArrayList(files)
                )
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("DEPRECATION")
        files = arguments?.getParcelableArrayList(ARG_FILES) ?: emptyList()
    }
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogGithubPushBinding.inflate(layoutInflater)
        
        setupViews()
        observeViewModel()
        
        return MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .setTitle("Push to GitHub")
            .setPositiveButton("Push") { _, _ ->
                pushToGitHub()
            }
            .setNegativeButton("Cancel", null)
            .create()
    }
    
    private fun setupViews() {
        binding.tvFileCount.text = "${files.size} files ready to push"
    }
    
    private fun pushToGitHub() {
        val token = binding.etGithubToken.text.toString().trim()
        val repoName = binding.etRepoName.text.toString().trim()
        val branch = binding.etBranch.text.toString().trim().ifEmpty { "main" }
        
        if (token.isEmpty()) {
            binding.etGithubToken.error = "Token required"
            return
        }
        
        if (repoName.isEmpty()) {
            binding.etRepoName.error = "Repo name required"
            return
        }
        
        viewModel.pushToGitHub(token, repoName, files, branch)
    }
    
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.pushState.collectLatest { state ->
                    when (state) {
                        is GitHubPushState.Idle -> {
                            binding.progressBar.visibility = View.GONE
                        }
                        is GitHubPushState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        is GitHubPushState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            dismiss()
                            // Show success
                        }
                        is GitHubPushState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            binding.tvError.text = state.message
                            binding.tvError.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
