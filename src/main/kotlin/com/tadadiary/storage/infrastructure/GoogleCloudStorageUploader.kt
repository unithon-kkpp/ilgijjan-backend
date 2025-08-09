package com.tadadiary.storage.infrastructure

import com.tadadiary.storage.application.FileUploader
import com.tadadiary.storage.presentation.StorageResponse
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile

@Component
class GoogleCloudStorageUploader : FileUploader {
    override fun upload(file: MultipartFile): StorageResponse {
        TODO("Not yet implemented")
    }
}