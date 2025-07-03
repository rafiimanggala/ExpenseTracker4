package com.example.expensetracker4.ui.expense

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.expensetracker4.R
import com.example.expensetracker4.data.Budget
import com.example.expensetracker4.data.Expense
import com.example.expensetracker4.data.ExpenseDao
import com.example.expensetracker4.data.MyDatabase
import com.example.expensetracker4.data.dao.BudgetDao
import com.example.expensetracker4.databinding.FragmentExpenseTrackerBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ExpenseTrackerFragment : Fragment() {

    private var _binding: FragmentExpenseTrackerBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: MyDatabase
    private lateinit var expenseDao: ExpenseDao
    private lateinit var budgetDao: BudgetDao

    private lateinit var expenseAdapter: ExpenseAdapter
    private var budgets: List<Budget> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExpenseTrackerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = MyDatabase.getDatabase(requireContext())
        expenseDao = db.expenseDao()
        budgetDao = db.budgetDao()

        setupRecyclerView()

        budgetDao.getAllBudgets().observe(viewLifecycleOwner) { budgetList ->
            budgets = budgetList
            loadExpensesAndUpdateAdapter()
        }

        // Pakai FloatingActionButton langsung dari binding
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

    private fun loadExpensesAndUpdateAdapter() {
        lifecycleScope.launch {
            val expenses = expenseDao.getAllExpensesSorted()
            expenseAdapter.updateData(expenses, budgets)
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
