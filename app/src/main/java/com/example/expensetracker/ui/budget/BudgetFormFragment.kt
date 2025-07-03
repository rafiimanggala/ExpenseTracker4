package com.example.expensetracker.ui.budget

import android.content.Context
import android.os.Bundle
import android.util.Log
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
        val sharedPref = requireContext().getSharedPreferences("session", Context.MODE_PRIVATE)
        val userId = sharedPref.getInt("userId", -1)
        Log.d("BudgetFormFragment", "User ID dari SharedPreferences: $userId")

        if (userId == -1) {
            Toast.makeText(requireContext(), "User ID tidak ditemukan", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.setUserId(userId)
        Log.d("BudgetFormFragment", "User ID diset di ViewModel: $userId")

        editingBudgetId = arguments?.getInt("budgetId")
        Log.d("BudgetFormFragment", "Editing budget ID: $editingBudgetId")

        if (editingBudgetId != null) {
            lifecycleScope.launch {
                editingBudget = withContext(Dispatchers.IO) {
                    viewModel.getById(editingBudgetId!!)
                }
                editingBudget?.let {
                    Log.d("BudgetFormFragment", "Edit mode: Budget ditemukan dengan ID ${it.id}")
                    setupEditBudget(it)
                } ?: Log.d("BudgetFormFragment", "Edit mode: Budget tidak ditemukan")
            }
        }

        binding.btnSaveBudget.setOnClickListener {
            Log.d("BudgetFormFragment", "Tombol simpan ditekan")
            saveBudget()
        }
    }

    private fun setupEditBudget(budget: Budget) {
        binding.etBudgetName.setText(budget.name)
        binding.etBudgetAmount.setText(budget.total.toString())

        Log.d("BudgetFormFragment", "Menyiapkan tampilan edit dengan nama: ${budget.name}, total: ${budget.total}")

        viewModel.getTotalExpenseForBudget(budget.id).observe(viewLifecycleOwner) { totalExpense ->
            totalExpenseForBudget = totalExpense
            Log.d("BudgetFormFragment", "Total pengeluaran untuk budget ID ${budget.id}: $totalExpenseForBudget")
        }
    }

    private fun saveBudget() {
        val name = binding.etBudgetName.text.toString().trim()
        val totalText = binding.etBudgetAmount.text.toString().trim()
        val total = totalText.toDoubleOrNull()

        Log.d("BudgetFormFragment", "Menyimpan budget dengan nama: $name, total: $totalText")

        if (name.isEmpty()) {
            Toast.makeText(requireContext(), "Nama budget harus diisi", Toast.LENGTH_SHORT).show()
            Log.d("BudgetFormFragment", "Validasi gagal: nama kosong")
            return
        }

        if (total == null || total < 0) {
            Toast.makeText(requireContext(), "Nominal budget tidak boleh negatif", Toast.LENGTH_SHORT).show()
            Log.d("BudgetFormFragment", "Validasi gagal: nominal tidak valid")
            return
        }

        if (editingBudget != null) {
            if (total < totalExpenseForBudget) {
                Toast.makeText(
                    requireContext(),
                    "Nominal budget tidak boleh kurang dari total pengeluaran ($totalExpenseForBudget)",
                    Toast.LENGTH_LONG
                ).show()
                Log.d("BudgetFormFragment", "Validasi gagal: total budget ($total) < total pengeluaran ($totalExpenseForBudget)")
                return
            }

            val updatedBudget = editingBudget!!.copy(
                name = name,
                total = total,
                amount = total
            )
            Log.d("BudgetFormFragment", "Mengupdate budget: $updatedBudget")

            lifecycleScope.launch {
                viewModel.update(updatedBudget)
                Log.d("BudgetFormFragment", "Budget berhasil diupdate")
                parentFragmentManager.popBackStack()
            }
        } else {
            val newBudget = Budget(
                name = name,
                total = total,
                amount = total,
                userId = viewModel.run {
                    val field = this::class.java.getDeclaredField("currentUserId")
                    field.isAccessible = true
                    field.get(this) as Int
                }
            )
            Log.d("BudgetFormFragment", "Menambahkan budget baru: $newBudget")

            lifecycleScope.launch {
                viewModel.insert(newBudget)
                Log.d("BudgetFormFragment", "Budget baru berhasil ditambahkan")
                parentFragmentManager.popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.d("BudgetFormFragment", "onDestroyView dipanggil - binding dihapus")
    }
}
