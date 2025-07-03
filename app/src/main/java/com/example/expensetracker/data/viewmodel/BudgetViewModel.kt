package com.example.expensetracker.ui.budget

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.Budget
import com.example.expensetracker.data.BudgetWithUsage
import com.example.expensetracker.data.repository.BudgetRepository
import kotlinx.coroutines.launch

class BudgetViewModel(private val repository: BudgetRepository) : ViewModel() {

    private val _userId = MutableLiveData<Int>()
    private var currentUserId: Int = -1  // Simpan userId sebagai variabel biasa

    val budgets = _userId.switchMap { userId ->
        repository.getAllBudgets(userId)
    }

    val budgetWithUsage = _userId.switchMap { userId ->
        repository.getBudgetWithUsage(userId)
    }

    fun setUserId(userId: Int) {
        _userId.value = userId
        currentUserId = userId
    }

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

    fun getTotalExpenseForBudget(budgetId: Int): LiveData<Double> {
        if (currentUserId == -1) throw IllegalStateException("User ID belum di-set")
        return repository.getTotalExpenseForBudget(budgetId, currentUserId)
    }

    suspend fun getById(id: Int): Budget? {
        if (currentUserId == -1) throw IllegalStateException("User ID belum di-set")
        return repository.getById(id, currentUserId)
    }
}


