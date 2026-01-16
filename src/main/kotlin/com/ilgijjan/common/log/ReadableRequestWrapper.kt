package com.ilgijjan.common.log

import jakarta.servlet.ReadListener
import jakarta.servlet.ServletInputStream
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.InputStreamReader

class ReadableRequestWrapper(request: HttpServletRequest) : HttpServletRequestWrapper(request) {
    private val bodyData: ByteArray = request.inputStream.readAllBytes()

    override fun getInputStream(): ServletInputStream {
        val byteArrayInputStream = ByteArrayInputStream(bodyData)
        return object : ServletInputStream() {
            override fun read(): Int = byteArrayInputStream.read()
            override fun isFinished(): Boolean = byteArrayInputStream.available() == 0
            override fun isReady(): Boolean = true
            override fun setReadListener(readListener: ReadListener?) {}
        }
    }

    override fun getReader(): BufferedReader {
        return BufferedReader(InputStreamReader(getInputStream()))
    }

    fun getBody(): String {
        return if (bodyData.isEmpty()) "" else String(bodyData, Charsets.UTF_8)
    }
}
