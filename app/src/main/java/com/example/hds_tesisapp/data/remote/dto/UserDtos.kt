package com.example.hds_tesisapp.data.remote.dto

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class PlayerResponse(
    val success: Boolean,
    val message: String,
    val data: PlayerDto?
)

data class PlayerDto(
    val uid: String,
    val username: String,
    val email: String
)
