package com.example.munidigital.model

import com.google.gson.annotations.SerializedName

data class User(
    val id: Int,
    val username: String,
    @SerializedName("full_name")
    val fullName: String?,
    @SerializedName("created_at")
    val createdAt: String
)
