package com.example.expensetracker.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.example.expensetracker.R
import com.example.expensetracker.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationBarView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        navController = navHostFragment.navController

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.expenseTrackerFragment -> {
                    navigateSingleTopTo(R.id.expenseTrackerFragment)
                    true
                }
                R.id.budgetListFragment -> {
                    navigateSingleTopTo(R.id.budgetListFragment)
                    true
                }
                R.id.reportFragment -> {         // Tambahan ini
                    navigateSingleTopTo(R.id.reportFragment)
                    true
                }
                else -> false
            }
        }

        binding.bottomNavigationView.selectedItemId = R.id.expenseTrackerFragment
    }

    private fun navigateSingleTopTo(destinationId: Int) {
        val navOptions = NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setPopUpTo(destinationId, inclusive = false)
            .build()

        navController.navigate(destinationId, null, navOptions)
    }
}

