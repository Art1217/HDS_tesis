package com.example.hds_tesisapp.data.repository

interface ProgressRepository {
    suspend fun saveProgress(uid: String, idLevel: Int, completed: Boolean): ApiResult<Unit>
    suspend fun getCompletedLevels(uid: String): ApiResult<Set<Int>>
}
