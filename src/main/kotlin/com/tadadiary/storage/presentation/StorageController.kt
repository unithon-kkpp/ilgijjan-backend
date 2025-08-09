package com.tadadiary.storage.presentation

import com.tadadiary.storage.application.FileUploader
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/storage")
class StorageController(
    private val fileUploader: FileUploader
) {

    @PostMapping("/upload")
    fun uploadFile(@RequestParam("file") file: MultipartFile): ResponseEntity<StorageResponse> {
        val response = fileUploader.upload(file)
        return ResponseEntity(response, HttpStatus.OK)
    }
}