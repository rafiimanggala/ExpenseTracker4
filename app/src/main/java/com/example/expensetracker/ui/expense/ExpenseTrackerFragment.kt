package com.example.expensetracker.ui.expense

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.expensetracker.R
import com.example.expensetracker.data.Budget
import com.example.expensetracker.data.Expense
import com.example.expensetracker.data.ExpenseDao
import com.example.expensetracker.data.MyDatabase
import com.example.expensetracker.data.dao.BudgetDao
import com.example.expensetracker.data.repository.BudgetRepository
import com.example.expensetracker.data.repository.ExpenseRepository
import com.example.expensetracker.data.viewmodel.ExpenseViewModel
import com.example.expensetracker.data.viewmodel.ExpenseViewModelFactory
import com.example.expensetracker.databinding.FragmentExpenseTrackerBinding
import com.example.expensetracker.ui.budget.BudgetViewModel
import com.example.expensetracker.ui.budget.BudgetViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ExpenseTrackerFragment : Fragment() {

    private var _binding: FragmentExpenseTrackerBinding? = null
    private val binding get() = _binding!!

    private lateinit var expenseAdapter: ExpenseAdapter
    private var budgets: List<Budget> = listOf()

    private val expenseViewModel: ExpenseViewModel by viewModels {
        ExpenseViewModelFactory(
            ExpenseRepository(
                MyDatabase.getDatabase(requireContext()).expenseDao()
            )
        )
    }

    private val budgetViewModel: BudgetViewModel by viewModels {
        BudgetViewModelFactory(
            BudgetRepository(
                MyDatabase.getDatabase(requireContext()).budgetDao(),
                MyDatabase.getDatabase(requireContext()).expenseDao()
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExpenseTrackerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        // Ambil userId dari SharedPreferences
        val sharedPref = requireContext().getSharedPreferences("session", Context.MODE_PRIVATE)
        val userId = sharedPref.getInt("userId", -1)
        Log.d("ExpenseTrackerFragment", "User ID: $userId")

        if (userId == -1) {
            Toast.makeText(requireContext(), "User ID tidak ditemukan", Toast.LENGTH_SHORT).show()
            return
        }

        // Set userId ke ViewModel
        expenseViewModel.setUserId(userId)
        budgetViewModel.setUserId(userId)

        // Observe budget
        budgetViewModel.budgets.observe(viewLifecycleOwner) { budgetList ->
            budgets = budgetList
            // Setelah budgets tersedia, observe expenses
            collectAllExpensesAndUpdateAdapter()
        }

        // FAB tambah pengeluaran
        binding.fabAddExpense.setOnClickListener {
            findNavController().navigate(R.id.action_expenseTrackerFragment_to_newExpenseFragment)
        }
    }

    private fun setupRecyclerView() {
        expenseAdapter = ExpenseAdapter(listOf(), budgets) { expense ->
            showExpenseDetailDialog(expense)
        }
        binding.recyclerViewExpenses.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = expenseAdapter
        }
    }

    private fun collectAllExpensesAndUpdateAdapter() {
        val allExpenses = mutableListOf<Expense>()

        budgets.forEach { budget ->
            expenseViewModel.getExpensesForBudget(budget.id).observe(viewLifecycleOwner) { expenses ->
                allExpenses.removeAll { it.budgetId == budget.id }
                allExpenses.addAll(expenses)
                expenseAdapter.updateData(allExpenses.sortedByDescending { it.date }, budgets)
            }
        }
    }

    private fun showExpenseDetailDialog(expense: Expense) {
        val budget = budgets.find { it.id == expense.budgetId }
        val dialogView = layoutInflater.inflate(R.layout.dialog_expense_detail, null)
        val formatter = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))

        dialogView.findViewById<TextView>(R.id.tvDate).text =
            formatter.format(Date(expense.date))
        dialogView.findViewById<TextView>(R.id.tvNote).text = expense.note
        dialogView.findViewById<TextView>(R.id.tvNominal).text = "Rp ${expense.amount.toInt()}"
        dialogView.findViewById<TextView>(R.id.tvBudget).text = budget?.name ?: "Unknown"

        AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("Tutup") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


