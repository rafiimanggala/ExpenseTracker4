package com.example.expensetracker.ui.budget

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.expensetracker.data.Budget
import com.example.expensetracker.databinding.ActivityBudgetFormBinding
import com.example.expensetracker.data.MyDatabase
import com.example.expensetracker.data.repository.BudgetRepository
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BudgetFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBudgetFormBinding

    private val viewModel: BudgetViewModel by viewModels {
        BudgetViewModelFactory(
            BudgetRepository(
                MyDatabase.getDatabase(this).budgetDao(),
                MyDatabase.getDatabase(this).expenseDao()
            )
        )
    }

    private var editingBudget: Budget? = null
    private var totalExpenseForBudget: Double = 0.0
    private var editingBudgetId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBudgetFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        editingBudgetId = intent.getIntExtra("budget_id", -1).takeIf { it != -1 }

        if (editingBudgetId != null) {
            // Load budget dari DB secara async
            lifecycleScope.launch {
                editingBudget = withContext(Dispatchers.IO) {
                    viewModel.getById(editingBudgetId!!)
                }
                editingBudget?.let {
                    setupEditBudget(it)
                }
            }
        }

        binding.btnSaveBudget.setOnClickListener {
            saveBudget()
        }
    }

    private fun setupEditBudget(budget: Budget) {
        binding.etBudgetName.setText(budget.name)
        binding.etBudgetAmount.setText(budget.total.toString()) // karena total adalah total anggaran

        // Observasi total expense untuk budget ini, update nilai used dan totalExpenseForBudget
        viewModel.getTotalExpenseForBudget(budget.id).observe(this) { totalExpense ->
            totalExpenseForBudget = totalExpense
        }
    }

    private fun saveBudget() {
        val name = binding.etBudgetName.text.toString().trim()
        val totalText = binding.etBudgetAmount.text.toString().trim()
        val total = totalText.toDoubleOrNull()

        if (name.isEmpty()) {
            Toast.makeText(this, "Nama budget harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        if (total == null || total < 0) {
            Toast.makeText(this, "Nominal budget tidak boleh negatif", Toast.LENGTH_SHORT).show()
            return
        }

        if (editingBudget != null) {
            if (total < totalExpenseForBudget) {
                Toast.makeText(
                    this,
                    "Nominal budget tidak boleh kurang dari total pengeluaran (${totalExpenseForBudget})",
                    Toast.LENGTH_LONG
                ).show()
                return
            }
            // Update Budget dengan total dan used yang sudah ada
            val updatedBudget = editingBudget!!.copy(
                name = name,
                total = total,
                amount = total,  // bisa set sama dengan total, atau sesuai kebutuhanmu
                used = editingBudget!!.used // used tetap diambil dari data lama, karena dihitung dari Expense
            )
            lifecycleScope.launch {
                viewModel.update(updatedBudget)
                finish()
            }
        } else {
            // Budget baru, used awal pasti 0, amount sama dengan total
            val newBudget = Budget(
                name = name,
                total = total,
                amount = total,
                used = 0.0
            )
            lifecycleScope.launch {
                viewModel.insert(newBudget)
                finish()
            }
        }
    }
}

