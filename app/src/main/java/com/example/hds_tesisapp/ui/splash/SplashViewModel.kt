package com.example.hds_tesisapp.ui.splash

import androidx.lifecycle.ViewModel
import com.example.hds_tesisapp.data.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val sessionRepository: SessionRepository
) : ViewModel() {

    suspend fun hasActiveSession(): Boolean = sessionRepository.currentUid.first() != null
}
