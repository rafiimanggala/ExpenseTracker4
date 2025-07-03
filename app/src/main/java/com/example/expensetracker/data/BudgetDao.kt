package com.example.expensetracker.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.expensetracker.data.Budget

@Dao
interface BudgetDao {



    @Insert
    suspend fun insertBudget(budget: Budget)

    @Update
    suspend fun updateBudget(budget: Budget)

    @Query("SELECT * FROM Budget WHERE id = :id")
    suspend fun getBudgetById(id: Int): Budget?

    @Delete
    suspend fun deleteBudget(budget: Budget)

    @Query("SELECT * FROM budget ORDER BY id DESC")
    fun getAllBudgets(): LiveData<List<Budget>>

}
