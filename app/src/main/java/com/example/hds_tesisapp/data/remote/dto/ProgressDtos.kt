package com.example.hds_tesisapp.data.remote.dto

data class ProgressSaveRequest(
    val uid: String,
    val progressData: ProgressDataDto
)

data class ProgressDataDto(
    val idLevel: Int,
    val completed: Boolean
)

data class SimpleResponse(
    val success: Boolean,
    val message: String
)
