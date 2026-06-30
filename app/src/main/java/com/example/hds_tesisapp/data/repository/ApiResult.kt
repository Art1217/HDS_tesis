package com.example.hds_tesisapp.data.repository

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Failure(val message: String, val code: Int? = null) : ApiResult<Nothing>()
}
