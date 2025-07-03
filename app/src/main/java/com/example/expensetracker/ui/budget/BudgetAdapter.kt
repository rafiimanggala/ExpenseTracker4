package com.example.expensetracker4.ui.budget

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker4.data.Budget
import com.example.expensetracker4.databinding.ItemBudgetBinding

class BudgetAdapter(private val onClick: (Budget) -> Unit) :
    ListAdapter<Budget, BudgetAdapter.BudgetViewHolder>(BudgetDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetViewHolder {
        val binding = ItemBudgetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BudgetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BudgetViewHolder, position: Int) {
        val budget = getItem(position)
        holder.bind(budget)
    }

    inner class BudgetViewHolder(private val binding: ItemBudgetBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(budget: Budget) {
            binding.tvBudgetName.text = budget.name
            binding.tvBudgetAmount.text = "Rp ${budget.amount}"
            binding.root.setOnClickListener { onClick(budget) }
        }
    }

    class BudgetDiffCallback : DiffUtil.ItemCallback<Budget>() {
        override fun areItemsTheSame(oldItem: Budget, newItem: Budget) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Budget, newItem: Budget) = oldItem == newItem
    }
}
