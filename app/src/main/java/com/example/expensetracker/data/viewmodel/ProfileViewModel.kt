package com.example.expensetracker.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _changePasswordResult = MutableStateFlow<Boolean?>(null)
    val changePasswordResult: StateFlow<Boolean?> = _changePasswordResult

    fun changePassword(username: String, oldPassword: String, newPassword: String, repeatPassword: String) {
        viewModelScope.launch {
            val user = userRepository.getUser(username, oldPassword)
            if (user == null) {
                _changePasswordResult.value = false // old password salah
                return@launch
            }

            if (newPassword != repeatPassword) {
                _changePasswordResult.value = false // password baru tidak cocok
                return@launch
            }

            userRepository.updatePassword(username, newPassword)
            _changePasswordResult.value = true
        }
    }

    fun resetChangePasswordResult() {
        _changePasswordResult.value = null
    }
}
