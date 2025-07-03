package com.example.expensetracker4.ui.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.expensetracker4.data.repository.BudgetRepository

class BudgetViewModelFactory(
    private val repository: BudgetRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BudgetViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BudgetViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
