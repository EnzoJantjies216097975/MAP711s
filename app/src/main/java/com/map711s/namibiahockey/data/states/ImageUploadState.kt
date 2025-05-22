package com.map711s.namibiahockey.data.states

data class ImageUploadState(
    val isUploading: Boolean = false,
    val isSuccess: Boolean = false,
    val progress: Float = 0f,
    val downloadUrl: String = "",
    val error: String? = null
)