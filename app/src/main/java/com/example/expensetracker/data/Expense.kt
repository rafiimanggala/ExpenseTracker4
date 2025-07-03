package com.example.expensetracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val amount: Double,
    val note: String,
    val date: Long, // Simpan dalam timestamp
    val budgetId: Int
)


