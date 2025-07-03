package com.example.expensetracker.ui.expense

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.expensetracker.data.Budget
import com.example.expensetracker.data.BudgetWithUsage
import com.example.expensetracker.data.Expense
import com.example.expensetracker.data.ExpenseDao
import com.example.expensetracker.data.MyDatabase
import com.example.expensetracker.data.dao.BudgetDao
import com.example.expensetracker.data.repository.BudgetRepository
import com.example.expensetracker.data.repository.ExpenseRepository
import com.example.expensetracker.data.viewmodel.ExpenseViewModel
import com.example.expensetracker.data.viewmodel.ExpenseViewModelFactory
import com.example.expensetracker.databinding.FragmentNewExpenseBinding
import com.example.expensetracker.ui.budget.BudgetViewModel
import com.example.expensetracker.ui.budget.BudgetViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NewExpenseFragment : Fragment() {

    private var _binding: FragmentNewExpenseBinding? = null
    private val binding get() = _binding!!

    private lateinit var expenseViewModel: ExpenseViewModel
    private lateinit var budgetViewModel: BudgetViewModel

    private var budgets: List<BudgetWithUsage> = listOf()
    private var userId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewExpenseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ambil userId dari SharedPreferences
        val sharedPref = requireContext().getSharedPreferences("session", Context.MODE_PRIVATE)
        userId = sharedPref.getInt("userId", -1)

        if (userId == -1) {
            Toast.makeText(requireContext(), "User ID tidak ditemukan. Silakan login ulang.", Toast.LENGTH_LONG).show()
            findNavController().popBackStack()
            return
        }

        // --- Inisialisasi Repository
        val budgetDao = MyDatabase.getDatabase(requireContext()).budgetDao()
        val expenseDao = MyDatabase.getDatabase(requireContext()).expenseDao()

        val budgetRepository = BudgetRepository(budgetDao, expenseDao)
        val expenseRepository = ExpenseRepository(expenseDao) // ini tergantung bagaimana ExpenseViewModel dibentuk

        // --- Inisialisasi ViewModel dengan Factory
        val budgetFactory = BudgetViewModelFactory(budgetRepository)
        val expenseFactory = ExpenseViewModelFactory(expenseRepository)

        budgetViewModel = ViewModelProvider(requireActivity(), budgetFactory)[BudgetViewModel::class.java]
        expenseViewModel = ViewModelProvider(requireActivity(), expenseFactory)[ExpenseViewModel::class.java]

        // --- Set User ID
        budgetViewModel.setUserId(userId)
        expenseViewModel.setUserId(userId)

        // --- Setup UI
        showTodayDate()
        observeBudgets()

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

    private fun observeBudgets() {
        budgetViewModel.budgetWithUsage.observe(viewLifecycleOwner) { loadedBudgets ->
            budgets = loadedBudgets

            if (budgets.isEmpty()) {
                Toast.makeText(requireContext(), "Belum ada budget yang tersedia.", Toast.LENGTH_LONG).show()
                binding.btnAddExpense.isEnabled = false
                return@observe
            }

            binding.btnAddExpense.isEnabled = true

            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                budgets.map { it.budget.name }
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerBudget.adapter = adapter

            updateProgressBar()
        }
    }

    private fun updateProgressBar() {
        if (budgets.isEmpty()) return

        val selectedBudget = budgets.getOrNull(binding.spinnerBudget.selectedItemPosition) ?: return

        val total = selectedBudget.budget.total
        val used = selectedBudget.used
        val remaining = total - used

        binding.progressBar.max = total.toInt()
        binding.progressBar.progress = used.toInt()
        binding.tvRemainingBudget.text = "Sisa budget: Rp ${remaining.toInt()}"
    }

    private fun addExpense() {
        val nominalText = binding.etNominal.text.toString().trim()
        val note = binding.etNote.text.toString().trim()

        if (nominalText.isEmpty()) {
            binding.etNominal.error = "Nominal tidak boleh kosong"
            return
        }

        val nominal = nominalText.toDoubleOrNull()
        if (nominal == null || nominal <= 0) {
            binding.etNominal.error = "Nominal harus angka dan lebih dari 0"
            return
        }

        if (budgets.isEmpty()) {
            Toast.makeText(requireContext(), "Tidak ada budget yang tersedia.", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedBudget = budgets[binding.spinnerBudget.selectedItemPosition]
        val remainingBudget = selectedBudget.budget.total - selectedBudget.used

        if (nominal > remainingBudget) {
            Toast.makeText(requireContext(), "Pengeluaran melebihi sisa budget: Rp ${remainingBudget.toInt()}", Toast.LENGTH_LONG).show()
            return
        }

        val expense = Expense(
            amount = nominal,
            note = note,
            date = System.currentTimeMillis(),
            budgetId = selectedBudget.budget.id,
            userId = userId
        )

        expenseViewModel.insert(expense)

        Toast.makeText(requireContext(), "Pengeluaran berhasil ditambahkan.", Toast.LENGTH_SHORT).show()
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}



