package com.aiagent.ui.home

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aiagent.data.model.Agent
import com.aiagent.databinding.ItemAgentGridBinding

class AgentGridAdapter(
    private val onAgentClick: (Agent) -> Unit
) : ListAdapter<Agent, AgentGridAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAgentGridBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemAgentGridBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(agent: Agent) {
            binding.apply {
                tvAgentIcon.text = agent.icon
                tvAgentName.text = agent.name
                tvAgentDescription.text = agent.description
                tvTemplateCount.text = "${agent.templateCount} templates"
                cardAgent.setCardBackgroundColor(Color.parseColor(agent.color))
                
                root.setOnClickListener {
                    onAgentClick(agent)
                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Agent>() {
        override fun areItemsTheSame(oldItem: Agent, newItem: Agent): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Agent, newItem: Agent): Boolean {
            return oldItem == newItem
        }
    }
}
