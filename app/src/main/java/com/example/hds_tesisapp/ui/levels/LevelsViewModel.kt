package com.example.hds_tesisapp.ui.levels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hds_tesisapp.data.repository.ApiResult
import com.example.hds_tesisapp.data.repository.ProgressRepository
import com.example.hds_tesisapp.data.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LevelsUiState(
    val completedLevels: Set<Int> = emptySet(),
    val isLoading: Boolean = false
)

@HiltViewModel
class LevelsViewModel @Inject constructor(
    private val progressRepository: ProgressRepository,
    private val sessionRepository: SessionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LevelsUiState())
    val uiState: StateFlow<LevelsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val uid = sessionRepository.currentUid.first()
            if (uid == null) {
                _uiState.update { it.copy(isLoading = false) }
                return@launch
            }
            when (val result = progressRepository.getCompletedLevels(uid)) {
                is ApiResult.Success -> _uiState.update { it.copy(completedLevels = result.data, isLoading = false) }
                is ApiResult.Failure -> _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}
