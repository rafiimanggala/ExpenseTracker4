package com.example.expensetracker4.ui.budget

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker4.data.Budget
import com.example.expensetracker4.data.repository.BudgetRepository
import kotlinx.coroutines.launch

class BudgetViewModel(private val repository: BudgetRepository) : ViewModel() {

    val budgets: LiveData<List<Budget>> = repository.allBudgets

    fun insert(budget: Budget) {
        viewModelScope.launch {
            repository.insert(budget)
        }
    }

    fun update(budget: Budget) {
        viewModelScope.launch {
            repository.update(budget)
        }
    }

    fun getTotalExpenseForBudget(budgetId: Int): LiveData<Double> =
        repository.getTotalExpenseForBudget(budgetId)

    suspend fun getById(id: Int): Budget? = repository.getById(id)
}
