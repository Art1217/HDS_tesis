package com.example.hds_tesisapp.data.repository

import com.example.hds_tesisapp.data.remote.ApiService
import com.example.hds_tesisapp.data.remote.dto.ProgressDataDto
import com.example.hds_tesisapp.data.remote.dto.ProgressSaveRequest
import com.example.hds_tesisapp.data.remote.dto.SimpleResponse
import com.example.hds_tesisapp.data.remote.dto.ProgressListResponse
import com.squareup.moshi.Moshi
import java.io.IOException
import javax.inject.Inject

class ProgressRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val moshi: Moshi
) : ProgressRepository {

    override suspend fun saveProgress(uid: String, idLevel: Int, completed: Boolean): ApiResult<Unit> {
        val response = try {
            apiService.saveProgress(ProgressSaveRequest(uid, ProgressDataDto(idLevel, completed)))
        } catch (e: IOException) {
            return ApiResult.Failure("No se pudo conectar al servidor")
        }
        val body = response.body()
        if (response.isSuccessful && body?.success == true) {
            return ApiResult.Success(Unit)
        }
        val message = body?.message
            ?: parseErrorMessage(response.errorBody()?.string())
            ?: "No se pudo guardar el progreso"
        return ApiResult.Failure(message, response.code())
    }

    override suspend fun getCompletedLevels(uid: String): ApiResult<Set<Int>> {
        val response = try {
            apiService.getProgress(uid)
        } catch (e: IOException) {
            return ApiResult.Failure("No se pudo conectar al servidor")
        }
        val body = response.body()
        if (response.isSuccessful && body?.success == true && body.data != null) {
            return ApiResult.Success(body.data.completedLevels.toSet())
        }
        val message = body?.message
            ?: parseErrorMessage(response.errorBody()?.string())
            ?: "No se pudo obtener el progreso"
        return ApiResult.Failure(message, response.code())
    }

    private fun parseErrorMessage(raw: String?): String? {
        if (raw.isNullOrBlank()) return null
        return try {
            moshi.adapter(SimpleResponse::class.java).fromJson(raw)?.message
        } catch (e: Exception) {
            null
        }
    }
}
