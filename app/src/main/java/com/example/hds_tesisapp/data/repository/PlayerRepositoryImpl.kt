package com.example.hds_tesisapp.data.repository

import com.example.hds_tesisapp.data.remote.ApiService
import com.example.hds_tesisapp.data.remote.dto.LoginRequest
import com.example.hds_tesisapp.data.remote.dto.PlayerDto
import com.example.hds_tesisapp.data.remote.dto.PlayerResponse
import com.example.hds_tesisapp.data.remote.dto.RegisterRequest
import com.example.hds_tesisapp.data.remote.dto.SimpleResponse
import com.example.hds_tesisapp.domain.model.Player
import com.squareup.moshi.Moshi
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

class PlayerRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val moshi: Moshi
) : PlayerRepository {

    override suspend fun register(username: String, email: String, password: String): ApiResult<Player> =
        safeCall { apiService.register(RegisterRequest(username, email, password)) }

    override suspend fun login(email: String, password: String): ApiResult<Player> =
        safeCall { apiService.login(LoginRequest(email, password)) }

    override suspend fun getUser(uid: String): ApiResult<Player> =
        safeCall { apiService.getUser(uid) }

    private suspend fun safeCall(block: suspend () -> Response<PlayerResponse>): ApiResult<Player> {
        val response = try {
            block()
        } catch (e: IOException) {
            return ApiResult.Failure("No se pudo conectar al servidor")
        }
        val body = response.body()
        if (response.isSuccessful && body?.success == true && body.data != null) {
            return ApiResult.Success(body.data.toDomain())
        }
        val message = body?.message
            ?: parseErrorMessage(response.errorBody()?.string())
            ?: "Error desconocido"
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

private fun PlayerDto.toDomain() = Player(uid = uid, username = username, email = email)
