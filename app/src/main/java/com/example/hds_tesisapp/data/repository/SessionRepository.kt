package com.example.hds_tesisapp.data.repository

import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    val currentUid: Flow<String?>
    suspend fun saveSession(uid: String)
    suspend fun clearSession()
}
