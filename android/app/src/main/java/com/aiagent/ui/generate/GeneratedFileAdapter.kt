package com.aiagent.ui.generate

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aiagent.data.model.GeneratedFile
import com.aiagent.databinding.ItemGeneratedFileBinding

class GeneratedFileAdapter(
    private val onViewClick: (GeneratedFile) -> Unit,
    private val onCopyClick: (GeneratedFile) -> Unit
) : ListAdapter<GeneratedFile, GeneratedFileAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemGeneratedFileBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemGeneratedFileBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(file: GeneratedFile) {
            binding.apply {
                tvFileName.text = file.path.substringAfterLast("/")
                tvFilePath.text = file.path
                tvFileSize.text = "${file.content.length} chars"
                tvLanguage.text = file.language
                
                btnView.setOnClickListener {
                    onViewClick(file)
                }
                
                btnCopy.setOnClickListener {
                    onCopyClick(file)
                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<GeneratedFile>() {
        override fun areItemsTheSame(oldItem: GeneratedFile, newItem: GeneratedFile): Boolean {
            return oldItem.path == newItem.path
        }

        override fun areContentsTheSame(oldItem: GeneratedFile, newItem: GeneratedFile): Boolean {
            return oldItem == newItem
        }
    }
}
