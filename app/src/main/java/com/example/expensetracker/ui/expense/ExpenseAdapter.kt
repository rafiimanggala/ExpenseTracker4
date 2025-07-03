package com.example.expensetracker4.ui.expense

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker4.data.Budget
import com.example.expensetracker4.data.Expense
import com.example.expensetracker4.databinding.ItemExpenseBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExpenseAdapter(
    private var expenses: List<Expense>,
    private var budgets: List<Budget>,
    private val onNominalClick: (Expense) -> Unit
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    inner class ExpenseViewHolder(val binding: ItemExpenseBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(expense: Expense) {
            val budget = budgets.find { it.id == expense.budgetId }
            val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
            binding.tvDate.text = dateFormat.format(Date(expense.date))
            binding.tvNominal.text = "Rp ${expense.amount.toInt()}"
            binding.chipBudget.text = budget?.name ?: "Unknown"

            binding.tvNominal.setOnClickListener {
                onNominalClick(expense)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val binding = ItemExpenseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExpenseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        holder.bind(expenses[position])
    }

    override fun getItemCount(): Int = expenses.size

    fun updateData(newExpenses: List<Expense>) {
        expenses = newExpenses
        notifyDataSetChanged()
    }
    fun updateData(newExpenses: List<Expense>, newBudgets: List<Budget>) {
        this.expenses = newExpenses
        this.budgets = newBudgets
        notifyDataSetChanged()
    }

}
