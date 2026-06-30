package com.example.hds_tesisapp.ui.progress

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

data class ProgressUiState(
    val isSaving: Boolean = false,
    val lastError: String? = null
)

@HiltViewModel
class ProgressViewModel @Inject constructor(
    private val progressRepository: ProgressRepository,
    private val sessionRepository: SessionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProgressUiState())
    val uiState: StateFlow<ProgressUiState> = _uiState.asStateFlow()

    fun saveLevelCompleted(idLevel: Int, onDone: () -> Unit) {
        viewModelScope.launch {
            val uid = sessionRepository.currentUid.first()
            if (uid == null) {
                onDone()
                return@launch
            }
            _uiState.update { it.copy(isSaving = true, lastError = null) }
            when (val result = progressRepository.saveProgress(uid, idLevel, completed = true)) {
                is ApiResult.Success -> _uiState.update { it.copy(isSaving = false) }
                is ApiResult.Failure -> _uiState.update { it.copy(isSaving = false, lastError = result.message) }
            }
            onDone()
        }
    }
}
