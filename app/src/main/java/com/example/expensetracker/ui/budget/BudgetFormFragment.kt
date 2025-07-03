package com.example.expensetracker.ui.budget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.expensetracker.data.Budget
import com.example.expensetracker.data.MyDatabase
import com.example.expensetracker.data.repository.BudgetRepository
import com.example.expensetracker.databinding.FragmentBudgetFormBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BudgetFormFragment : Fragment() {

    private var _binding: FragmentBudgetFormBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BudgetViewModel by viewModels {
        BudgetViewModelFactory(
            BudgetRepository(
                MyDatabase.getDatabase(requireContext()).budgetDao(),
                MyDatabase.getDatabase(requireContext()).expenseDao()
            )
        )
    }

    private var editingBudget: Budget? = null
    private var totalExpenseForBudget: Double = 0.0
    private var editingBudgetId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBudgetFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        editingBudgetId = arguments?.getInt("budgetId")

        if (editingBudgetId != null) {
            lifecycleScope.launch {
                editingBudget = withContext(Dispatchers.IO) {
                    viewModel.getById(editingBudgetId!!)
                }
                editingBudget?.let { setupEditBudget(it) }
            }
        }

        binding.btnSaveBudget.setOnClickListener {
            saveBudget()
        }
    }

    private fun setupEditBudget(budget: Budget) {
        binding.etBudgetName.setText(budget.name)
        binding.etBudgetAmount.setText(budget.total.toString())

        viewModel.getTotalExpenseForBudget(budget.id).observe(viewLifecycleOwner) { totalExpense ->
            totalExpenseForBudget = totalExpense
        }
    }

    private fun saveBudget() {
        val name = binding.etBudgetName.text.toString().trim()
        val totalText = binding.etBudgetAmount.text.toString().trim()
        val total = totalText.toDoubleOrNull()

        if (name.isEmpty()) {
            Toast.makeText(requireContext(), "Nama budget harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        if (total == null || total < 0) {
            Toast.makeText(requireContext(), "Nominal budget tidak boleh negatif", Toast.LENGTH_SHORT).show()
            return
        }

        if (editingBudget != null) {
            if (total < totalExpenseForBudget) {
                Toast.makeText(
                    requireContext(),
                    "Nominal budget tidak boleh kurang dari total pengeluaran ($totalExpenseForBudget)",
                    Toast.LENGTH_LONG
                ).show()
                return
            }

            val updatedBudget = editingBudget!!.copy(
                name = name,
                total = total,
                amount = total
            )
            lifecycleScope.launch {
                viewModel.update(updatedBudget)
                parentFragmentManager.popBackStack()
            }
        } else {
            val newBudget = Budget(
                name = name,
                total = total,
                amount = total
            )
            lifecycleScope.launch {
                viewModel.insert(newBudget)
                parentFragmentManager.popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
