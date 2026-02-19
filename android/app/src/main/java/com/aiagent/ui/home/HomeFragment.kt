package com.aiagent.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.aiagent.databinding.FragmentHomeBinding
import com.aiagent.ui.chat.ChatActivity
import com.aiagent.ui.generate.GenerateFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var agentAdapter: AgentGridAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupAgentGrid()
        setupButtons()
    }
    
    private fun setupAgentGrid() {
        agentAdapter = AgentGridAdapter { agent ->
            // Navigate to generate with selected agent
            val fragment = GenerateFragment()
            val bundle = Bundle().apply {
                putString("agent_id", agent.id)
            }
            fragment.arguments = bundle
            
            parentFragmentManager.beginTransaction()
                .replace(com.aiagent.R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }
        
        binding.recyclerAgents.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = agentAdapter
        }
        
        agentAdapter.submitList(com.aiagent.data.model.AgentData.agents)
    }
    
    private fun setupButtons() {
        binding.btnQuickChat.setOnClickListener {
            startActivity(Intent(requireContext(), ChatActivity::class.java))
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
