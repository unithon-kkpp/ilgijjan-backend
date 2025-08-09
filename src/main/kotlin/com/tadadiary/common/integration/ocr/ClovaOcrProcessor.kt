package com.tadadiary.common.integration.ocr

import com.tadadiary.common.integration.ocr.OcrProcessor
import org.springframework.stereotype.Component

@Component
class ClovaOcrProcessor : OcrProcessor {
    override fun extractText(photoUrl: String): String {
        // Naver CLOVA OCR API 호출
        return "추출된 텍스트"
    }
}