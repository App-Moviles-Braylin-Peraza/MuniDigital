package com.example.munidigital.model

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    val username: String,
    val password: String,
    @SerializedName("full_name")
    val fullName: String?
)
