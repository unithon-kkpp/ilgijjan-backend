package com.ilgijjan.integration.ocr.infrastructure

import com.google.api.gax.core.FixedCredentialsProvider
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.vision.v1.AnnotateImageRequest
import com.google.cloud.vision.v1.Feature
import com.google.cloud.vision.v1.Image
import com.google.cloud.vision.v1.ImageAnnotatorClient
import com.google.cloud.vision.v1.ImageAnnotatorSettings
import com.google.cloud.vision.v1.ImageSource
import com.ilgijjan.integration.ocr.application.OcrProcessor
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Primary
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Component
import java.io.IOException

@Primary
@Component
class GoogleVisionOcrProcessor(
    @Value("\${spring.cloud.gcp.storage.credentials.location}")
    private val keyPath: String,
    private val resourceLoader: ResourceLoader
) : OcrProcessor {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun extractText(photoUrl: String): String {
        log.info("Google Vision OCR 요청 시작 - photoUrl: {}", photoUrl)

        try {
            val resource = resourceLoader.getResource(keyPath)
            val credentials = GoogleCredentials.fromStream(resource.inputStream)

            val settings = ImageAnnotatorSettings.newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                .build()

            ImageAnnotatorClient.create(settings).use { client ->
                val imgSource = ImageSource.newBuilder().setImageUri(photoUrl).build()
                val img = Image.newBuilder().setSource(imgSource).build()
                val feat = Feature.newBuilder().setType(Feature.Type.DOCUMENT_TEXT_DETECTION).build()

                val request = AnnotateImageRequest.newBuilder()
                    .addFeatures(feat)
                    .setImage(img)
                    .build()

                val response = client.batchAnnotateImages(listOf(request))
                val responses = response.responsesList

                val resultBuilder = StringBuilder()

                for (res in responses) {
                    if (res.hasError()) {
                        log.error("Google Vision API Error: {}", res.error.message)
                        throw RuntimeException("OCR 처리 실패: ${res.error.message}")
                    }
                    resultBuilder.append(res.fullTextAnnotation.text)
                    resultBuilder.append(" ")
                }

                val extractedText = resultBuilder.toString().trim()
                log.info("추출된 텍스트 길이: {}", extractedText.length)

                return extractedText
            }
        } catch (e: IOException) {
            log.error("Google Vision OCR 호출 중 I/O 에러 (키 파일 확인 필요)", e)
            throw RuntimeException("Google OCR 호출 실패", e)
        } catch (e: Exception) {
            log.error("Google Vision OCR 처리 중 알 수 없는 에러", e)
            throw e
        }
    }
}
