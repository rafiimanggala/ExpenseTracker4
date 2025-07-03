package com.example.expensetracker4.ui.profile

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.expensetracker4.data.MyDatabase
import com.example.expensetracker4.data.repository.UserRepository
import com.example.expensetracker4.data.viewmodel.ProfileViewModel
import com.example.expensetracker4.data.viewmodel.ProfileViewModelFactory
import com.example.expensetracker4.databinding.FragmentProfileBinding
import com.example.expensetracker4.ui.SignInActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var viewModel: ProfileViewModel
    private lateinit var sharedPref: SharedPreferences
    private var isOldPasswordCorrect = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        sharedPref =
            requireActivity().getSharedPreferences("session", AppCompatActivity.MODE_PRIVATE)
        val username = sharedPref.getString("username", "") ?: ""

        val userDao = MyDatabase.getDatabase(requireContext()).userDao()
        val repository = UserRepository(userDao)
        val factory = ProfileViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[ProfileViewModel::class.java]

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.btnChangePassword.isEnabled = false

        // Fungsi validasi gabungan
        fun validateChangePasswordButton() {
            val newPass = binding.etNewPassword.text.toString()
            val repeatPass = binding.etRepeatPassword.text.toString()

            binding.btnChangePassword.isEnabled =
                isOldPasswordCorrect && newPass.isNotBlank() && repeatPass.isNotBlank() && newPass == repeatPass
        }

        // Tambahkan TextWatcher untuk newPass dan repeatPass
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateChangePasswordButton()
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        binding.etNewPassword.addTextChangedListener(textWatcher)
        binding.etRepeatPassword.addTextChangedListener(textWatcher)

        // Tombol cek password lama
        binding.btnCheckOldPassword.setOnClickListener {
            val oldPass = binding.etOldPassword.text.toString()

            lifecycleScope.launch {
                val user = repository.getUserByUsername(username)
                if (user != null && user.password == oldPass) {
                    Toast.makeText(
                        requireContext(),
                        "Password lama benar",
                        Toast.LENGTH_SHORT
                    ).show()
                    isOldPasswordCorrect = true
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Password lama salah",
                        Toast.LENGTH_SHORT
                    ).show()
                    isOldPasswordCorrect = false
                }
                validateChangePasswordButton()
            }
        }

        // Tombol change password
        binding.btnChangePassword.setOnClickListener {
            val oldPass = binding.etOldPassword.text.toString()
            val newPass = binding.etNewPassword.text.toString()
            val repeatPass = binding.etRepeatPassword.text.toString()

            viewModel.changePassword(username, oldPass, newPass, repeatPass)
        }

        // Tombol logout
        binding.btnSignOut.setOnClickListener {
            sharedPref.edit().clear().apply()
            val intent = Intent(requireContext(), SignInActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        // Observasi hasil perubahan password
        lifecycleScope.launch {
            viewModel.changePasswordResult.collectLatest { result ->
                when (result) {
                    true -> Toast.makeText(
                        requireContext(),
                        "Password updated",
                        Toast.LENGTH_SHORT
                    ).show()
                    false -> Toast.makeText(
                        requireContext(),
                        "Failed to update password",
                        Toast.LENGTH_SHORT
                    ).show()
                    null -> {}
                }
                viewModel.resetChangePasswordResult()
            }
        }
    }
}
