package com.example.expensetracker.ui.expense

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.expensetracker.data.Budget
import com.example.expensetracker.data.BudgetWithUsage
import com.example.expensetracker.data.Expense
import com.example.expensetracker.data.ExpenseDao
import com.example.expensetracker.data.MyDatabase
import com.example.expensetracker.data.dao.BudgetDao
import com.example.expensetracker.databinding.FragmentNewExpenseBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NewExpenseFragment : Fragment() {

    private var _binding: FragmentNewExpenseBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: MyDatabase
    private lateinit var budgetDao: BudgetDao
    private lateinit var expenseDao: ExpenseDao

    // Ganti tipe dari Budget ke BudgetWithUsage
    private var budgets: List<BudgetWithUsage> = listOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNewExpenseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        db = MyDatabase.getDatabase(requireContext())
        budgetDao = db.budgetDao()
        expenseDao = db.expenseDao()

        showTodayDate()
        loadBudgetsWithUsage()

        binding.spinnerBudget.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                updateProgressBar()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        binding.btnAddExpense.setOnClickListener {
            addExpense()
        }
    }

    private fun showTodayDate() {
        val today = System.currentTimeMillis()
        val formatter = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
        binding.tvDate.text = formatter.format(Date(today))
    }

    private fun loadBudgetsWithUsage() {
        budgetDao.getBudgetWithUsage().observe(viewLifecycleOwner) { loadedBudgets ->
            budgets = loadedBudgets

            // Spinner menampilkan nama budget dari BudgetWithUsage.budget.name
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, budgets.map { it.budget.name })
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerBudget.adapter = adapter

            updateProgressBar()
        }
    }

    private fun updateProgressBar() {
        if (budgets.isEmpty()) return

        val selectedBudgetWithUsage = budgets.getOrNull(binding.spinnerBudget.selectedItemPosition) ?: return

        val total = selectedBudgetWithUsage.budget.total
        val used = selectedBudgetWithUsage.used
        val remaining = total - used

        binding.progressBar.max = total.toInt()
        binding.progressBar.progress = used.toInt()
        binding.tvRemainingBudget.text = "Sisa budget: Rp ${remaining.toInt()}"
    }

    private fun addExpense() {
        val nominalText = binding.etNominal.text.toString()
        val note = binding.etNote.text.toString()

        if (nominalText.isEmpty()) {
            Toast.makeText(context, "Nominal tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }

        val nominal = nominalText.toDoubleOrNull()
        if (nominal == null || nominal <= 0) {
            Toast.makeText(context, "Nominal harus lebih dari 0", Toast.LENGTH_SHORT).show()
            return
        }

        if (budgets.isEmpty()) {
            Toast.makeText(context, "Tidak ada budget yang tersedia", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedBudgetWithUsage = budgets[binding.spinnerBudget.selectedItemPosition]
        val remainingBudget = selectedBudgetWithUsage.budget.total - selectedBudgetWithUsage.used
        if (nominal > remainingBudget) {
            Toast.makeText(context, "Nominal melebihi sisa budget: Rp ${remainingBudget.toInt()}", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val expense = Expense(
                amount = nominal,
                note = note,
                date = System.currentTimeMillis(),
                budgetId = selectedBudgetWithUsage.budget.id
            )
            expenseDao.insertExpense(expense)

            // Tidak perlu update Budget.used secara manual, karena ini dihitung otomatis dari relasi expense

            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Expense berhasil ditambah", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
