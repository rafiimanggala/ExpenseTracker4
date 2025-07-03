package com.example.expensetracker.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.expensetracker.data.Budget
import com.example.expensetracker.data.BudgetWithUsage

@Dao
interface BudgetDao {

    @Insert
    suspend fun insertBudget(budget: Budget)

    @Update
    suspend fun updateBudget(budget: Budget)

    @Query("SELECT * FROM Budget WHERE id = :id AND userId = :userId")
    suspend fun getBudgetById(id: Int, userId: Int): Budget?


    @Delete
    suspend fun deleteBudget(budget: Budget)

    @Query("SELECT * FROM budget WHERE userId = :userId ORDER BY id DESC")
    fun getAllBudgets(userId: Int): LiveData<List<Budget>>

    @Query("""
        SELECT b.*, 
        IFNULL(SUM(e.amount), 0) AS used 
        FROM budget b 
        LEFT JOIN expense e ON b.id = e.budgetId 
        WHERE b.userId = :userId
        GROUP BY b.id
    """)
    fun getBudgetWithUsage(userId: Int): LiveData<List<BudgetWithUsage>>
}

