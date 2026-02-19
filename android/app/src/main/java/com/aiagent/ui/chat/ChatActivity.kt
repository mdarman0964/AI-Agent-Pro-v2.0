package com.aiagent.ui.chat

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
nimport androidx.recyclerview.widget.LinearLayoutManager
import com.aiagent.data.model.ChatMessage
import com.aiagent.databinding.ActivityChatBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityChatBinding
    private val viewModel: ChatViewModel by viewModels()
    private lateinit var chatAdapter: ChatAdapter
    
    companion object {
        private const val EXTRA_AGENT_ID = "agent_id"
        
        fun newIntent(context: Context, agentId: String?): Intent {
            return Intent(context, ChatActivity::class.java).apply {
                putExtra(EXTRA_AGENT_ID, agentId)
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        setupRecyclerView()
        setupInput()
        observeViewModel()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = "AI Assistant"
            setDisplayHomeAsUpEnabled(true)
        }
    }
    
    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter()
        binding.recyclerChat.apply {
            layoutManager = LinearLayoutManager(this@ChatActivity)
            adapter = chatAdapter
        }
    }
    
    private fun setupInput() {
        binding.btnSend.setOnClickListener {
            val message = binding.etMessage.text.toString().trim()
            if (message.isNotEmpty()) {
                viewModel.sendMessage(message)
                binding.etMessage.text?.clear()
            }
        }
    }
    
    private fun observeViewModel() {
        viewModel.messages.observe(this) { messages ->
            chatAdapter.submitList(messages)
            binding.recyclerChat.scrollToPosition(messages.size - 1)
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
