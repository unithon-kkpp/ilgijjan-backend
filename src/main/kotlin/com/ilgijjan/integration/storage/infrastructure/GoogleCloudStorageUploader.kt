package com.ilgijjan.integration.storage.infrastructure

import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import com.ilgijjan.integration.storage.application.FileUploader
import com.ilgijjan.integration.storage.presentation.StorageResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.net.HttpURLConnection
import java.net.URI
import java.nio.channels.Channels
import java.util.UUID

@Component
class GoogleCloudStorageUploader(
    private val storage: Storage,
    @Value("\${spring.cloud.gcp.storage.bucket}")
    private val bucketName: String
) : FileUploader {

    override fun upload(file: MultipartFile): StorageResponse {
        val uuid = UUID.randomUUID().toString()
        val contentType = file.contentType
        val blobInfo = BlobInfo.newBuilder(bucketName, uuid)
            .setContentType(contentType)
            .build()

        storage.writer(blobInfo).use { writeChannel ->
            Channels.newOutputStream(writeChannel).use { outputStream ->
                file.inputStream.copyTo(outputStream)
            }
        }

        return StorageResponse(generateFileUrl(uuid))
    }

    override fun uploadFromUrl(imageUrl: String): String {
        val uuid = UUID.randomUUID().toString()

        val uri = URI.create(imageUrl)
        val connection = uri.toURL().openConnection() as HttpURLConnection
        connection.requestMethod = "GET"

        connection.inputStream.use { inputStream ->
            val blobInfo = BlobInfo.newBuilder(bucketName, uuid)
                .setContentType("image/png")
                .build()

            storage.writer(blobInfo).use { writeChannel ->
                Channels.newOutputStream(writeChannel).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        }

        return generateFileUrl(uuid)
    }

    private fun generateFileUrl(fileName: String): String {
        return "https://storage.googleapis.com/$bucketName/$fileName"
    }
}
