package com.example.expensetracker4.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.expensetracker4.data.MyDatabase
import com.example.expensetracker4.data.User
import com.example.expensetracker4.data.repository.UserRepository
import com.example.expensetracker4.data.viewmodel.AuthViewModel
import com.example.expensetracker4.data.viewmodel.AuthViewModelFactory
import com.example.expensetracker4.databinding.ActivitySignUpBinding
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val database = MyDatabase.getDatabase(applicationContext)
        val userRepository = UserRepository(database.userDao())
        val factory = AuthViewModelFactory(userRepository)
        authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        binding.btnSignUp.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val firstName = binding.etFirstName.text.toString().trim()
            val lastName = binding.etLastName.text.toString().trim()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()

            if (username.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val existingUser = userRepository.getUserByUsername(username)
                if (existingUser != null) {
                    Log.d("SignUpActivity", "Username already exists: $username")
                    Toast.makeText(this@SignUpActivity, "Username already exists!", Toast.LENGTH_SHORT).show()
                } else {
                    val newUser = User(
                        username = username,
                        password = password,
                        firstName = firstName,
                        lastName = lastName
                    )
                    userRepository.insertUser(newUser)

                    // Verifikasi dengan mengambil ulang data dari DB
                    val insertedUser = userRepository.getUserByUsername(username)
                    Log.d("SignUpActivity", "User inserted: $insertedUser")

                    Toast.makeText(this@SignUpActivity, "Registration successful", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
}
