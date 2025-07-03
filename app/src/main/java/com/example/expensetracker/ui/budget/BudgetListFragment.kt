package com.example.expensetracker.ui.budget

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.expensetracker.R
import com.example.expensetracker.data.Budget
import com.example.expensetracker.databinding.FragmentBudgetListBinding
import com.example.expensetracker.data.MyDatabase
import com.example.expensetracker.data.repository.BudgetRepository

class BudgetListFragment : Fragment() {

    private var _binding: FragmentBudgetListBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: BudgetAdapter

    private val viewModel: BudgetViewModel by viewModels {
        BudgetViewModelFactory(
            BudgetRepository(
                MyDatabase.getDatabase(requireContext()).budgetDao(),
                MyDatabase.getDatabase(requireContext()).expenseDao()
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBudgetListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = BudgetAdapter { budget -> onBudgetClicked(budget) }
        binding.recyclerViewBudgets.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewBudgets.adapter = adapter

        // Tombol tambah budget (FAB)
        binding.fabAddBudget.setOnClickListener {
            // Navigasi ke BudgetFormFragment tanpa argumen
            findNavController().navigate(R.id.budgetFormFragment)
        }

        // Observe data dari ViewModel
        viewModel.budgets.observe(viewLifecycleOwner) { budgets ->
            adapter.submitList(budgets)
        }
    }

    private fun onBudgetClicked(budget: Budget) {
        // Navigasi ke BudgetFormFragment sambil mengirim budgetId
        val bundle = Bundle().apply {
            putInt("budgetId", budget.id)
        }
        findNavController().navigate(R.id.budgetFormFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
