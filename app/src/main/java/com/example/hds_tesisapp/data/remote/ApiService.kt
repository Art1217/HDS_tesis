package com.example.hds_tesisapp.data.remote

import com.example.hds_tesisapp.data.remote.dto.LoginRequest
import com.example.hds_tesisapp.data.remote.dto.PlayerResponse
import com.example.hds_tesisapp.data.remote.dto.ProgressSaveRequest
import com.example.hds_tesisapp.data.remote.dto.RegisterRequest
import com.example.hds_tesisapp.data.remote.dto.SimpleResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("user/register")
    suspend fun register(@Body body: RegisterRequest): Response<PlayerResponse>

    @POST("user/login")
    suspend fun login(@Body body: LoginRequest): Response<PlayerResponse>

    @GET("user/{uid}")
    suspend fun getUser(@Path("uid") uid: String): Response<PlayerResponse>

    @POST("progress/save")
    suspend fun saveProgress(@Body body: ProgressSaveRequest): Response<SimpleResponse>
}
