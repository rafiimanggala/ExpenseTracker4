package com.example.expensetracker.data.repository

import androidx.lifecycle.LiveData
import com.example.expensetracker.data.Budget
import com.example.expensetracker.data.ExpenseDao
import com.example.expensetracker.data.dao.BudgetDao

class BudgetRepository(
    private val budgetDao: BudgetDao,
    private val expenseDao: ExpenseDao
) {
    val allBudgets: LiveData<List<Budget>> = budgetDao.getAllBudgets()

    suspend fun insert(budget: Budget) = budgetDao.insertBudget(budget)

    suspend fun update(budget: Budget) = budgetDao.updateBudget(budget)

    suspend fun getById(id: Int): Budget? = budgetDao.getBudgetById(id)

    fun getTotalExpenseForBudget(budgetId: Int): LiveData<Double> =
        expenseDao.getTotalExpenseForBudget(budgetId)
}
