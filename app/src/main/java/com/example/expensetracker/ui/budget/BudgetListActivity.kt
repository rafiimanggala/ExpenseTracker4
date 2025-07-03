package com.example.expensetracker4.ui.budget

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.expensetracker4.R

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
