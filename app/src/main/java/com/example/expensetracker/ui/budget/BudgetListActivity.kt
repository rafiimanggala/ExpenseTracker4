package com.example.expensetracker.ui.budget

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.expensetracker.R

class BudgetListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget_list)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, BudgetListFragment())
                .commit()
        }
    }
}
