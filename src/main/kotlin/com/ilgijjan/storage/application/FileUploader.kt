package com.ilgijjan.storage.application

import com.ilgijjan.storage.presentation.StorageResponse
import org.springframework.web.multipart.MultipartFile

interface FileUploader {
    fun upload(file: MultipartFile): StorageResponse
}