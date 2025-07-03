package com.example.expensetracker4.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker4.data.User
import com.example.expensetracker4.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val userRepository: UserRepository) : ViewModel() {

    // loginSuccess nullable, null = belum login, true = sukses, false = gagal
    private val _loginSuccess = MutableStateFlow<Boolean?>(null)
    val loginSuccess: StateFlow<Boolean?> get() = _loginSuccess

    // signUp success or error message
    private val _signUpSuccess = MutableStateFlow<Boolean?>(null)
    val signUpSuccess: StateFlow<Boolean?> get() = _signUpSuccess

    private val _signUpError = MutableStateFlow<String?>(null)
    val signUpError: StateFlow<String?> get() = _signUpError

    private val _loginAttempted = MutableStateFlow(false)
    val loginAttempted: StateFlow<Boolean> get() = _loginAttempted


    fun signUp(username: String, password: String, firstName: String, lastName: String) {
        viewModelScope.launch {
            if (userRepository.isUsernameTaken(username)) {
                _signUpError.value = "Username already exists"
                _signUpSuccess.value = false
            } else {
                val user = User(
                    username = username,
                    password = password,
                    firstName = firstName,
                    lastName = lastName
                )
                userRepository.insertUser(user)
                _signUpError.value = null
                _signUpSuccess.value = true
            }
        }
    }


    fun signIn(username: String, password: String) {
        viewModelScope.launch {
            _loginAttempted.value = true
            val user = userRepository.getUser(username, password)
            _loginSuccess.value = user != null
        }
    }

    fun resetLoginStatus() {
        _loginSuccess.value = null
        _loginAttempted.value = false
    }

    fun resetSignUpStatus() {
        _signUpSuccess.value = null
        _signUpError.value = null
    }
    fun resetLoginAttempted() {
        _loginAttempted.value = false
    }

}
