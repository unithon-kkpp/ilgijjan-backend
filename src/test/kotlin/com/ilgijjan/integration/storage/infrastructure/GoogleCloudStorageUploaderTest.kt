package com.ilgijjan.integration.storage.infrastructure

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@Disabled
@SpringBootTest
class GoogleCloudStorageUploaderTest @Autowired constructor(
    private val googleCloudStorageUploader: GoogleCloudStorageUploader
) {
    @Test
    fun uploadFromUrl() {
        val replicateImageUrl = "https://replicate.delivery/.../output.png"
        val gcsUrl = googleCloudStorageUploader.uploadFromUrl(replicateImageUrl)

        println("업로드된 GCS URL: $gcsUrl")

        assertTrue(gcsUrl.startsWith("https://storage.googleapis.com/"))
    }
}