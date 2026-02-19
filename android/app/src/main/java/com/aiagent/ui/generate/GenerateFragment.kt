package com.aiagent.ui.generate

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.aiagent.R
import com.aiagent.data.model.Agent
import com.aiagent.data.model.AgentData
import com.aiagent.data.model.GeneratedFile
import com.aiagent.data.model.ProjectTemplates
import com.aiagent.databinding.FragmentGenerateBinding
import com.aiagent.service.DownloadService
import com.aiagent.ui.chat.ChatActivity
import com.aiagent.utils.FileUtils
import com.aiagent.utils.LocaleHelper
import com.aiagent.utils.VoiceInputHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GenerateFragment : Fragment() {
    
    private var _binding: FragmentGenerateBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GenerateViewModel by viewModels()
    
    private lateinit var voiceHelper: VoiceInputHelper
    private lateinit var templateAdapter: TemplateAdapter
    private lateinit var fileAdapter: GeneratedFileAdapter
    
    private var selectedAgent: Agent? = null
    private var generatedFiles: List<GeneratedFile> = emptyList()
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startVoiceInput()
        } else {
            Snackbar.make(binding.root, "Permission denied", Snackbar.LENGTH_SHORT).show()
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGenerateBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        voiceHelper = VoiceInputHelper(requireContext())
        
        setupAgentSpinner()
        setupTemplateRecyclerView()
        setupFileRecyclerView()
        setupButtons()
        observeViewModel()
    }
    
    private fun setupAgentSpinner() {
        val isBangla = LocaleHelper.getLanguage(requireContext()) == "bn"
        val agentNames = AgentData.agents.map { if (isBangla) it.nameBn else it.name }
        
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, agentNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerAgent.adapter = adapter
        
        binding.spinnerAgent.onItemSelectedListener = 
            object : android.widget.AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                    selectedAgent = AgentData.agents[position]
                    updateTemplatesForAgent(selectedAgent!!)
                }
                override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
            }
    }
    
    private fun setupTemplateRecyclerView() {
        templateAdapter = TemplateAdapter { template ->
            binding.etProjectName.setText(template.name)
            binding.etDescription.setText(template.description)
            binding.etFeatures.setText(template.defaultFeatures.joinToString(", "))
        }
        
        binding.recyclerTemplates.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = templateAdapter
        }
    }
    
    private fun setupFileRecyclerView() {
        fileAdapter = GeneratedFileAdapter(
            onViewClick = { file ->
                // Open code viewer
                openCodeViewer(file)
            },
            onCopyClick = { file ->
                copyToClipboard(file.content)
            }
        )
        
        binding.recyclerFiles.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = fileAdapter
        }
    }
    
    private fun setupButtons() {
        // Generate button
        binding.btnGenerate.setOnClickListener {
            generateProject()
        }
        
        // Voice input
        binding.btnVoiceInput.setOnClickListener {
            checkAudioPermission()
        }
        
        // Download ZIP
        binding.btnDownloadZip.setOnClickListener {
            downloadProject()
        }
        
        // Share
        binding.btnShare.setOnClickListener {
            shareProject()
        }
        
        // Push to GitHub
        binding.btnPushGithub.setOnClickListener {
            pushToGitHub()
        }
        
        // Save to history
        binding.btnSaveHistory.setOnClickListener {
            saveToHistory()
        }
        
        // Chat with AI
        binding.btnChatAi.setOnClickListener {
            startActivity(ChatActivity.newIntent(requireContext(), selectedAgent?.id))
        }
    }
    
    private fun updateTemplatesForAgent(agent: Agent) {
        val templates = ProjectTemplates.getByAgentType(agent.id)
        templateAdapter.submitList(templates)
    }
    
    private fun generateProject() {
        val agent = selectedAgent ?: return
        val projectName = binding.etProjectName.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val featuresText = binding.etFeatures.text.toString().trim()
        
        if (projectName.isEmpty()) {
            binding.etProjectName.error = getString(R.string.project_name)
            return
        }
        
        if (description.isEmpty()) {
            binding.etDescription.error = getString(R.string.description)
            return
        }
        
        val features = featuresText.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        
        viewModel.generateProject(agent, projectName, description, features)
    }
    
    private fun checkAudioPermission() {
        when {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) 
                == PackageManager.PERMISSION_GRANTED -> {
                startVoiceInput()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO) -> {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Microphone Permission")
                    .setMessage("Need microphone access for voice input")
                    .setPositiveButton("Grant") { _, _ ->
                        requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }
    
    private fun startVoiceInput() {
        voiceHelper.startListening(
            onResult = { text ->
                binding.etDescription.setText(text)
            },
            onError = { error ->
                Snackbar.make(binding.root, error, Snackbar.LENGTH_SHORT).show()
            }
        )
    }
    
    private fun downloadProject() {
        if (generatedFiles.isEmpty()) return
        
        val projectName = binding.etProjectName.text.toString().trim()
        
        // Check storage permission for Android 10 and below
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 100)
                return
            }
        }
        
        DownloadService.startDownload(requireContext(), projectName, generatedFiles)
        Snackbar.make(binding.root, "Downloading...", Snackbar.LENGTH_SHORT).show()
    }
    
    private fun shareProject() {
        if (generatedFiles.isEmpty()) return
        
        val projectName = binding.etProjectName.text.toString().trim()
        DownloadService.startShare(requireContext(), projectName, generatedFiles)
    }
    
    private fun pushToGitHub() {
        if (generatedFiles.isEmpty()) return
        
        // Show GitHub dialog
        GitHubPushDialog.newInstance(generatedFiles)
            .show(childFragmentManager, "github_push")
    }
    
    private fun saveToHistory() {
        if (generatedFiles.isEmpty()) return
        
        viewModel.saveToHistory(
            projectName = binding.etProjectName.text.toString().trim(),
            description = binding.etDescription.text.toString().trim(),
            files = generatedFiles
        )
        
        Snackbar.make(binding.root, "Saved to history!", Snackbar.LENGTH_SHORT).show()
    }
    
    private fun openCodeViewer(file: GeneratedFile) {
        // Start CodeEditorActivity
        val intent = com.aiagent.ui.codeeditor.CodeEditorActivity.newIntent(
            requireContext(),
            file.path,
            file.content,
            file.language
        )
        startActivity(intent)
    }
    
    private fun copyToClipboard(text: String) {
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText("Code", text)
        clipboard.setPrimaryClip(clip)
        Snackbar.make(binding.root, "Copied to clipboard!", Snackbar.LENGTH_SHORT).show()
    }
    
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    when (state) {
                        is GenerateUiState.Idle -> {
                            showLoading(false)
                        }
                        is GenerateUiState.Loading -> {
                            showLoading(true)
                        }
                        is GenerateUiState.Success -> {
                            showLoading(false)
                            showGeneratedFiles(state.response.files)
                            generatedFiles = state.response.files
                        }
                        is GenerateUiState.Error -> {
                            showLoading(false)
                            Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }
    
    private fun showLoading(show: Boolean) {
        binding.progressBar.isVisible = show
        binding.btnGenerate.isEnabled = !show
        binding.btnGenerate.text = if (show) getString(R.string.generating) else getString(R.string.generate_project)
    }
    
    private fun showGeneratedFiles(files: List<GeneratedFile>) {
        binding.cardResult.isVisible = true
        fileAdapter.submitList(files)
        
        // Show file count
        binding.tvFileCount.text = "${files.size} files generated"
        
        // Show project structure
        binding.tvProjectStructure.text = generateStructurePreview(files)
    }
    
    private fun generateStructurePreview(files: List<GeneratedFile>): String {
        val dirs = files.map { it.path.substringBeforeLast("/", "") }.distinct().sorted()
        return dirs.joinToString("\n") { "📁 $it" }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        voiceHelper.destroy()
        _binding = null
    }
}
