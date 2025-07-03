package com.example.expensetracker.ui.report

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.expensetracker.data.MyDatabase
import com.example.expensetracker.data.repository.BudgetRepository
import com.example.expensetracker.databinding.FragmentReportBinding
import com.example.expensetracker.ui.budget.BudgetViewModel
import com.example.expensetracker.ui.budget.BudgetViewModelFactory

class ReportFragment : Fragment() {

    private var _binding: FragmentReportBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BudgetViewModel by viewModels {
        BudgetViewModelFactory(
            BudgetRepository(
                MyDatabase.getDatabase(requireContext()).budgetDao(),
                MyDatabase.getDatabase(requireContext()).expenseDao()
            )
        )
    }

    private val adapter = ReportAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recyclerViewReport.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewReport.adapter = adapter

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
