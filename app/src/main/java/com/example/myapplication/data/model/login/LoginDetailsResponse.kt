package com.example.myapplication.data.model.login

data class LoginDetailsResponse(
    val access_token: String,
    val user_id: String,
    val username: String,
    val role: String,
    val status: String
)