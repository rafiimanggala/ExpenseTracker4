package com.example.expensetracker.ui.report

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.expensetracker.data.MyDatabase
import com.example.expensetracker.data.repository.BudgetRepository
import com.example.expensetracker.databinding.FragmentReportBinding
import com.example.expensetracker.ui.budget.BudgetViewModel
import com.example.expensetracker.ui.budget.BudgetViewModelFactory

class ReportFragment : Fragment() {

    private var _binding: FragmentReportBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: BudgetViewModel
    private val adapter = ReportAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ambil user ID dari SharedPreferences
        val sharedPref = requireContext().getSharedPreferences("session", Context.MODE_PRIVATE)
        val userId = sharedPref.getInt("userId", -1)

        if (userId == -1) {
            Toast.makeText(requireContext(), "User ID tidak ditemukan. Silakan login ulang.", Toast.LENGTH_LONG).show()
            return
        }

        // Inisialisasi Repository dan ViewModel
        val budgetDao = MyDatabase.getDatabase(requireContext()).budgetDao()
        val expenseDao = MyDatabase.getDatabase(requireContext()).expenseDao()
        val repository = BudgetRepository(budgetDao, expenseDao)
        val factory = BudgetViewModelFactory(repository)

        viewModel = ViewModelProvider(this, factory)[BudgetViewModel::class.java]
        viewModel.setUserId(userId)

        // Setup RecyclerView
        binding.recyclerViewReport.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewReport.adapter = adapter

        // Observe budgetWithUsage
        viewModel.budgetWithUsage.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)

            val totalUsed = list.sumOf { it.used }
            val totalMax = list.sumOf { it.budget.total }
            binding.textViewTotal.text = "Total: Rp$totalUsed / Rp$totalMax"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

