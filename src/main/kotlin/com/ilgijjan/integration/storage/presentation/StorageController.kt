package com.ilgijjan.integration.storage.presentation

import com.ilgijjan.integration.storage.application.FileUploader
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/storage")
@Tag(name = "Storage", description = "Storage 관련 API입니다.")
class StorageController(
    private val fileUploader: FileUploader
) {

    @PostMapping("/upload", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @Operation(summary = "파일 업로드")
    fun uploadFile(@RequestParam("file") file: MultipartFile): ResponseEntity<StorageResponse> {
        val response = fileUploader.upload(file)
        return ResponseEntity(response, HttpStatus.OK)
    }
}