package com.example.expensetracker4.ui.report

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker4.data.BudgetWithUsage
import com.example.expensetracker4.databinding.ItemReportBinding

class ReportAdapter : RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {

    private val items = mutableListOf<BudgetWithUsage>()

    fun submitList(newList: List<BudgetWithUsage>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }

    inner class ReportViewHolder(private val binding: ItemReportBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: BudgetWithUsage) {
            val total = item.budget.total
            val used = item.used
            val remaining = total - used

            val percent = if (total == 0.0) 0 else ((used / total) * 100).toInt()

            binding.textBudgetName.text = item.budget.name
            binding.textUsed.text = "IDR $used"
            binding.textMax.text = "IDR $total"
            binding.textRemaining.text = "Budget left: IDR $remaining"
            binding.progressBar.progress = percent
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val binding = ItemReportBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReportViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        holder.bind(items[position])
    }
}
