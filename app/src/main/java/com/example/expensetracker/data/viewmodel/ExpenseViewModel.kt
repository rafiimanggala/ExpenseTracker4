package com.example.expensetracker.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.Expense
import com.example.expensetracker.data.repository.ExpenseRepository
import kotlinx.coroutines.launch

class ExpenseViewModel(private val repository: ExpenseRepository) : ViewModel() {

    private val _userId = MutableLiveData<Int>()

    fun setUserId(userId: Int) {
        _userId.value = userId
    }

    fun insert(expense: Expense) {
        viewModelScope.launch {
            repository.insert(expense)
        }
    }

    fun update(expense: Expense) {
        viewModelScope.launch {
            repository.update(expense)
        }
    }

    fun delete(expense: Expense) {
        viewModelScope.launch {
            repository.delete(expense)
        }
    }

    fun getTotalExpenseForBudget(budgetId: Int): LiveData<Double> =
        _userId.switchMap { userId ->
            repository.getTotalExpenseForBudget(budgetId, userId)
        }

    fun getExpensesForBudget(budgetId: Int): LiveData<List<Expense>> =
        _userId.switchMap { userId ->
            repository.getExpensesForBudget(budgetId, userId)
        }

    suspend fun getAllExpensesSorted(): List<Expense> {
        val userId = _userId.value ?: return emptyList()
        return repository.getAllExpensesSorted(userId)
    }
}
