package com.example.munidigital.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.munidigital.model.LoginRequest
import com.example.munidigital.model.LoginResponse
import com.example.munidigital.repository.AuthRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun login(loginRequest: LoginRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = authRepository.login(loginRequest)
                _loginResult.value = LoginResult(success = response)
            } catch (e: Exception) {
                _loginResult.value = LoginResult(error = e.message)
            } finally {
                _isLoading.value = false
            }
        }
    }
}

data class LoginResult(
    val success: LoginResponse? = null,
    val error: String? = null
)
