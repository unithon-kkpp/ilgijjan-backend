package com.tadadiary.storage.application

import com.tadadiary.storage.presentation.StorageResponse
import org.springframework.web.multipart.MultipartFile

interface FileUploader {
    fun upload(file: MultipartFile): StorageResponse
}