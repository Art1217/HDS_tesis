package com.example.hds_tesisapp.ui.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hds_tesisapp.ui.theme.Baloo2FontFamily
import com.example.hds_tesisapp.ui.theme.menu.HudMenuButton

@Composable
fun LoginScreen(
    onLoginSuccess: (uid: String) -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.loggedInPlayer) {
        uiState.loggedInPlayer?.let { onLoginSuccess(it.uid) }
    }

    AuthBackground {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .widthIn(max = 420.dp)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AuthTitle("Iniciar Sesión", accentColor = AuthAccentCyan)
                Spacer(Modifier.height(24.dp))
                AuthGlassPanel(accentColor = AuthAccentCyan) {
                    HudTextField(
                        value = uiState.email,
                        onValueChange = viewModel::onEmailChange,
                        label = "Email",
                        leadingIcon = Icons.Default.Email,
                        accentColor = AuthAccentCyan
                    )
                    Spacer(Modifier.height(14.dp))
                    HudTextField(
                        value = uiState.password,
                        onValueChange = viewModel::onPasswordChange,
                        label = "Contraseña",
                        leadingIcon = Icons.Default.Lock,
                        accentColor = AuthAccentCyan,
                        isPassword = true
                    )
                    uiState.errorMessage?.let {
                        Spacer(Modifier.height(14.dp))
                        HudErrorMessage(it)
                    }
                    Spacer(Modifier.height(22.dp))
                    HudMenuButton(
                        text = if (uiState.isLoading) "Entrando..." else "Entrar",
                        accentColor = AuthAccentCyan,
                        buttonHeight = 56.dp,
                        fontSize = 16,
                        enabled = !uiState.isLoading,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = viewModel::login
                    )
                    Spacer(Modifier.height(12.dp))
                    TextButton(onClick = onNavigateToRegister) {
                        Text(
                            "¿No tienes cuenta? Regístrate",
                            color = AuthAccentCyan,
                            fontFamily = Baloo2FontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}
