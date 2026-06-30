package com.example.hds_tesisapp.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.sessionDataStore by preferencesDataStore(name = "session")
private val UID_KEY = stringPreferencesKey("uid")

class SessionRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SessionRepository {

    override val currentUid: Flow<String?> = context.sessionDataStore.data.map { it[UID_KEY] }

    override suspend fun saveSession(uid: String) {
        context.sessionDataStore.edit { it[UID_KEY] = uid }
    }

    override suspend fun clearSession() {
        context.sessionDataStore.edit { it.remove(UID_KEY) }
    }
}
