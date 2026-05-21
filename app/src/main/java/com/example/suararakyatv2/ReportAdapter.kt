package com.example.suararakyatv2

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.suararakyatv2.databinding.ItemReportBinding
import java.text.SimpleDateFormat
import java.util.Locale

class ReportAdapter(
    private val onItemClick: (String) -> Unit
) : ListAdapter<Report, ReportAdapter.ReportViewHolder>(ReportDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val binding = ItemReportBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ReportViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ReportViewHolder(private val binding: ItemReportBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(report: Report) {
            binding.tvReportTitle.text = report.title
            binding.tvReportDescription.text = report.description
            binding.tvReportStatus.text = report.status
            
            report.timestamp?.let { date ->
                val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                binding.tvReportDate.text = sdf.format(date)
            }
            binding.root.setOnClickListener { onItemClick(report.id) }
        }
    }

    class ReportDiffCallback : DiffUtil.ItemCallback<Report>() {
        override fun areItemsTheSame(oldItem: Report, newItem: Report): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Report, newItem: Report): Boolean = oldItem == newItem
    }
}