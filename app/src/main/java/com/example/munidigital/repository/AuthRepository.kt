package com.example.munidigital.repository

import com.example.munidigital.model.LoginRequest
import com.example.munidigital.model.LoginResponse
import com.example.munidigital.model.RegisterRequest
import com.example.munidigital.model.User
import com.example.munidigital.network.ApiService

class AuthRepository(private val apiService: ApiService) {

    suspend fun login(loginRequest: LoginRequest): LoginResponse {
        return apiService.login(loginRequest)
    }

    suspend fun register(registerRequest: RegisterRequest): User {
        return apiService.register(registerRequest)
    }
}