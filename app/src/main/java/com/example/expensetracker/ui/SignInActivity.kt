package com.example.expensetracker4.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.expensetracker4.data.MyDatabase
import com.example.expensetracker4.data.repository.UserRepository
import com.example.expensetracker4.data.viewmodel.AuthViewModel
import com.example.expensetracker4.data.viewmodel.AuthViewModelFactory
import com.example.expensetracker4.databinding.ActivitySignInBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var authViewModel: AuthViewModel
    private lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)

        // 🟡 Cek apakah user sudah login sebelumnya
        val sharedPref = getSharedPreferences("session", MODE_PRIVATE)
        val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)
        if (isLoggedIn) {
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("username", sharedPref.getString("username", ""))
                putExtra("firstName", sharedPref.getString("firstName", ""))
                putExtra("lastName", sharedPref.getString("lastName", ""))
            }
            startActivity(intent)
            finish()
            return
        }

        setContentView(binding.root)

        // Inisialisasi ViewModel & Repository
        val database = MyDatabase.getDatabase(applicationContext)
        userRepository = UserRepository(database.userDao())
        val factory = AuthViewModelFactory(userRepository)
        authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        // Tombol Sign In
        binding.btnSignIn.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            authViewModel.signIn(username, password)
        }

        // Tombol Sign Up
        binding.btnSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        // Observer login result
        lifecycleScope.launch {
            authViewModel.loginSuccess.collectLatest { success ->
                when (success) {
                    true -> {
                        val username = binding.etUsername.text.toString().trim()
                        val password = binding.etPassword.text.toString()

                        val user = userRepository.getUser(username, password)
                        if (user != null) {
                            // 🟢 Simpan sesi login ke SharedPreferences
                            with(sharedPref.edit()) {
                                putString("username", user.username)
                                putString("firstName", user.firstName)
                                putString("lastName", user.lastName)
                                putBoolean("isLoggedIn", true)
                                apply()
                            }

                            // Pindah ke halaman sukses
                            val intent = Intent(
                                this@SignInActivity,
                                MainActivity::class.java
                            ).apply {
                                putExtra("username", user.username)
                                putExtra("firstName", user.firstName)
                                putExtra("lastName", user.lastName)
                            }
                            startActivity(intent)
                            finish()
                        }

                        // Reset login status agar tidak trigger ulang
                        authViewModel.resetLoginStatus()
                    }

                    false -> {
                        Toast.makeText(this@SignInActivity, "Login failed", Toast.LENGTH_SHORT).show()
                        authViewModel.resetLoginStatus()
                    }

                    null -> {
                        // Do nothing
                    }
                }
            }
        }
    }
}
