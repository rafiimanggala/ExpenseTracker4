package com.example.expensetracker.data.repository

import androidx.lifecycle.LiveData
import com.example.expensetracker.data.Budget
import com.example.expensetracker.data.BudgetWithUsage
import com.example.expensetracker.data.ExpenseDao
import com.example.expensetracker.data.dao.BudgetDao

class BudgetRepository(
    private val budgetDao: BudgetDao,
    private val expenseDao: ExpenseDao
) {
    suspend fun insert(budget: Budget) = budgetDao.insertBudget(budget)

    suspend fun update(budget: Budget) = budgetDao.updateBudget(budget)

    suspend fun getById(id: Int, userId: Int): Budget? = budgetDao.getBudgetById(id, userId)

    fun getAllBudgets(userId: Int): LiveData<List<Budget>> =
        budgetDao.getAllBudgets(userId)

    fun getBudgetWithUsage(userId: Int): LiveData<List<BudgetWithUsage>> =
        budgetDao.getBudgetWithUsage(userId)

    fun getTotalExpenseForBudget(budgetId: Int, userId: Int): LiveData<Double> =
        expenseDao.getTotalExpenseForBudget(budgetId, userId)

}


