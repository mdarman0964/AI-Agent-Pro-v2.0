package com.aiagent.ui.generate

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aiagent.data.model.ProjectTemplate
import com.aiagent.databinding.ItemTemplateBinding

class TemplateAdapter(
    private val onTemplateClick: (ProjectTemplate) -> Unit
) : ListAdapter<ProjectTemplate, TemplateAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTemplateBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemTemplateBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(template: ProjectTemplate) {
            binding.apply {
                tvTemplateIcon.text = template.icon
                tvTemplateName.text = template.name
                tvTemplateDescription.text = template.description
                
                root.setOnClickListener {
                    onTemplateClick(template)
                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ProjectTemplate>() {
        override fun areItemsTheSame(oldItem: ProjectTemplate, newItem: ProjectTemplate): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ProjectTemplate, newItem: ProjectTemplate): Boolean {
            return oldItem == newItem
        }
    }
}
