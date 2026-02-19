package com.aiagent.ui.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aiagent.data.model.ChatMessage
import com.aiagent.databinding.ItemChatMessageBinding

class ChatAdapter : ListAdapter<ChatMessage, ChatAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemChatMessageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemChatMessageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(message: ChatMessage) {
            binding.apply {
                if (message.role == "user") {
                    layoutUser.visibility = View.VISIBLE
                    layoutAi.visibility = View.GONE
                    tvUserMessage.text = message.content
                } else {
                    layoutUser.visibility = View.GONE
                    layoutAi.visibility = View.VISIBLE
                    tvAiMessage.text = message.content
                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ChatMessage>() {
        override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem == newItem
        }
    }
}
