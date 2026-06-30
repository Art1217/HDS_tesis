package com.example.hds_tesisapp.data.repository

import com.example.hds_tesisapp.domain.model.Player

interface PlayerRepository {
    suspend fun register(username: String, email: String, password: String): ApiResult<Player>
    suspend fun login(email: String, password: String): ApiResult<Player>
    suspend fun getUser(uid: String): ApiResult<Player>
}
