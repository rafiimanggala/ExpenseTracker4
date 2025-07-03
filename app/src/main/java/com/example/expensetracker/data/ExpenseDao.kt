package com.example.expensetracker4.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update


@Dao
interface ExpenseDao {

    @Query("SELECT IFNULL(SUM(amount), 0) FROM Expense WHERE budgetId = :budgetId")
    fun getTotalExpenseForBudget(budgetId: Int): LiveData<Double>

    @Query("SELECT * FROM Expense WHERE budgetId = :budgetId")
    fun getExpensesForBudget(budgetId: Int): LiveData<List<Expense>>

    @Query("SELECT * FROM Expense ORDER BY date DESC")
    suspend fun getAllExpensesSorted(): List<Expense>

    @Insert
    suspend fun insertExpense(expense: Expense)

    @Update
    suspend fun updateExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

}

