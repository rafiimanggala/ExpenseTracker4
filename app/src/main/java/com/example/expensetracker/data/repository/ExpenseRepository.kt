package com.example.expensetracker.data.repository

import androidx.lifecycle.LiveData
import com.example.expensetracker.data.Expense
import com.example.expensetracker.data.ExpenseDao

class ExpenseRepository(private val expenseDao: ExpenseDao) {

    suspend fun insert(expense: Expense) = expenseDao.insertExpense(expense)

    suspend fun update(expense: Expense) = expenseDao.updateExpense(expense)

    suspend fun delete(expense: Expense) = expenseDao.deleteExpense(expense)

    fun getTotalExpenseForBudget(budgetId: Int, userId: Int): LiveData<Double> =
        expenseDao.getTotalExpenseForBudget(budgetId, userId)

    fun getExpensesForBudget(budgetId: Int, userId: Int): LiveData<List<Expense>> =
        expenseDao.getExpensesForBudget(budgetId, userId)

    suspend fun getAllExpensesSorted(userId: Int): List<Expense> =
        expenseDao.getAllExpensesSorted(userId)
}
