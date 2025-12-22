package com.ilgijjan.integration.storage.application

import com.ilgijjan.integration.storage.presentation.StorageResponse
import org.springframework.web.multipart.MultipartFile

interface FileUploader {
    fun upload(file: MultipartFile): StorageResponse
    fun upload(fileData: ByteArray): String
    fun uploadFromUrl(imageUrl: String): String
}