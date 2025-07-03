package com.example.expensetracker4.ui.report

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker4.R
import com.example.expensetracker4.data.BudgetWithUsage

class ReportAdapter : RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {

    private val items = mutableListOf<BudgetWithUsage>()

    fun submitList(newList: List<BudgetWithUsage>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }

    inner class ReportViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: BudgetWithUsage) {
            val total = item.budget.total
            val used = item.used
            val remaining = total - used

            view.findViewById<TextView>(R.id.textBudgetName).text = item.budget.name
            view.findViewById<TextView>(R.id.textUsed).text = "IDR $used"
            view.findViewById<TextView>(R.id.textMax).text = "IDR $total"
            view.findViewById<TextView>(R.id.textRemaining).text = "Budget left: IDR $remaining"

            val percent = if (total == 0.0) 0 else ((used / total) * 100).toInt()
            view.findViewById<ProgressBar>(R.id.progressBar).progress = percent
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_report, parent, false)
        return ReportViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        holder.bind(items[position])
    }
}
