package com.example.hds_tesisapp.ui.auth

import com.example.hds_tesisapp.domain.model.Player

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val loggedInPlayer: Player? = null
)

data class RegisterUiState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val registeredPlayer: Player? = null
)
