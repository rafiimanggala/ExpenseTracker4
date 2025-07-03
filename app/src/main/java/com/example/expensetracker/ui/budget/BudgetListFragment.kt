package com.example.expensetracker.ui.budget

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
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

        // Ambil userId dari SharedPreferences (dengan nama dan key YANG BENAR)
        val sharedPref = requireContext().getSharedPreferences("session", Context.MODE_PRIVATE)
        val userId = sharedPref.getInt("userId", -1)

        Log.d("BudgetListFragment", "User ID dari SharedPreferences: $userId")

        if (userId == -1) {
            Toast.makeText(requireContext(), "User ID tidak ditemukan", Toast.LENGTH_SHORT).show()
            return
        }

        // Set userId ke ViewModel
        viewModel.setUserId(userId)

        // Observe setelah userId diset
        viewModel.budgets.observe(viewLifecycleOwner) { budgets ->
            adapter.submitList(budgets)
        }

        // Tombol tambah budget (FAB)
        binding.fabAddBudget.setOnClickListener {
            Log.d("BudgetListFragment", "FAB ditekan, navigasi ke BudgetFormFragment")
            findNavController().navigate(R.id.budgetFormFragment)
        }
    }



    private fun onBudgetClicked(budget: Budget) {
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

