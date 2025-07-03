package com.example.expensetracker.data

import androidx.room.ColumnInfo
import androidx.room.Embedded

data class BudgetWithUsage(
    @Embedded val budget: Budget,
    @ColumnInfo(name = "used") val used: Double  // ganti jadi Double supaya konsisten
)
