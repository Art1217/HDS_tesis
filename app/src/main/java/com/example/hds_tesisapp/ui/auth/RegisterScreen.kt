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
import androidx.compose.material.icons.filled.Person
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
fun RegisterScreen(
    onRegisterSuccess: (uid: String) -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.registeredPlayer) {
        uiState.registeredPlayer?.let { onRegisterSuccess(it.uid) }
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
                AuthTitle("Crear Cuenta", accentColor = AuthAccentMagenta)
                Spacer(Modifier.height(24.dp))
                AuthGlassPanel(accentColor = AuthAccentMagenta) {
                    HudTextField(
                        value = uiState.username,
                        onValueChange = viewModel::onUsernameChange,
                        label = "Usuario",
                        leadingIcon = Icons.Default.Person,
                        accentColor = AuthAccentMagenta
                    )
                    Spacer(Modifier.height(14.dp))
                    HudTextField(
                        value = uiState.email,
                        onValueChange = viewModel::onEmailChange,
                        label = "Email",
                        leadingIcon = Icons.Default.Email,
                        accentColor = AuthAccentMagenta
                    )
                    Spacer(Modifier.height(14.dp))
                    HudTextField(
                        value = uiState.password,
                        onValueChange = viewModel::onPasswordChange,
                        label = "Contraseña",
                        leadingIcon = Icons.Default.Lock,
                        accentColor = AuthAccentMagenta,
                        isPassword = true
                    )
                    uiState.errorMessage?.let {
                        Spacer(Modifier.height(14.dp))
                        HudErrorMessage(it)
                    }
                    Spacer(Modifier.height(22.dp))
                    HudMenuButton(
                        text = if (uiState.isLoading) "Creando..." else "Registrarme",
                        accentColor = AuthAccentMagenta,
                        buttonHeight = 56.dp,
                        fontSize = 16,
                        enabled = !uiState.isLoading,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = viewModel::register
                    )
                    Spacer(Modifier.height(12.dp))
                    TextButton(onClick = onNavigateToLogin) {
                        Text(
                            "¿Ya tienes cuenta? Inicia sesión",
                            color = AuthAccentMagenta,
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
