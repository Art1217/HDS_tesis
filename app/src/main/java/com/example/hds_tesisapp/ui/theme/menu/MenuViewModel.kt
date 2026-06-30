package com.example.hds_tesisapp.ui.theme.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hds_tesisapp.data.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MenuUiState(
    val isLoggingOut: Boolean = false
)

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val sessionRepository: SessionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MenuUiState())
    val uiState: StateFlow<MenuUiState> = _uiState.asStateFlow()

    fun logout(onDone: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoggingOut = true) }
            sessionRepository.clearSession()
            _uiState.update { it.copy(isLoggingOut = false) }
            onDone()
        }
    }
}
