package com.example.expensetracker.ui.profile

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.expensetracker.data.MyDatabase
import com.example.expensetracker.data.repository.UserRepository
import com.example.expensetracker.data.viewmodel.ProfileViewModel
import com.example.expensetracker.data.viewmodel.ProfileViewModelFactory
import com.example.expensetracker.databinding.FragmentProfileBinding
import com.example.expensetracker.ui.SignInActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var viewModel: ProfileViewModel
    private lateinit var sharedPref: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        sharedPref = requireActivity().getSharedPreferences("session", AppCompatActivity.MODE_PRIVATE)
        val username = sharedPref.getString("username", "") ?: ""

        val userDao = MyDatabase.getDatabase(requireContext()).userDao()
        val repository = UserRepository(userDao)
        val factory = ProfileViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[ProfileViewModel::class.java]

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.btnChangePassword.setOnClickListener {
            val oldPass = binding.etOldPassword.text.toString()
            val newPass = binding.etNewPassword.text.toString()
            val repeatPass = binding.etRepeatPassword.text.toString()

            viewModel.changePassword(username, oldPass, newPass, repeatPass)
        }

        binding.btnSignOut.setOnClickListener {
            sharedPref.edit().clear().apply()
            val intent = Intent(requireContext(), SignInActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        lifecycleScope.launch {
            viewModel.changePasswordResult.collectLatest { result ->
                when (result) {
                    true -> Toast.makeText(requireContext(), "Password updated", Toast.LENGTH_SHORT).show()
                    false -> Toast.makeText(requireContext(), "Failed to update password", Toast.LENGTH_SHORT).show()
                    null -> {}
                }
                viewModel.resetChangePasswordResult()
            }
        }
    }
}
