package com.ilgijjan.common.log

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.classic.spi.IThrowableProxy
import ch.qos.logback.core.AppenderBase
import com.ilgijjan.common.utils.DateFormatter
import com.ilgijjan.common.utils.StringUtil
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import java.time.Instant

class DiscordAppender : AppenderBase<ILoggingEvent>() {

    companion object {
        private const val MAX_MESSAGE_LENGTH = 500
        private const val MAX_STACKTRACE_LENGTH = 800
        private const val TIMEOUT_SECONDS = 5L
        private const val STACKTRACE_LINES = 5
    }

    var webhookUrl: String? = null
    var enabled: Boolean = true
    var applicationName: String = "ilgijjan"

    override fun append(event: ILoggingEvent) {
        if (!enabled || webhookUrl.isNullOrBlank()) {
            return
        }

        try {
            val message = buildDiscordMessage(event)
            sendToDiscord(message)
        } catch (e: Exception) {
            addError("Failed to send Discord notification", e)
        }
    }

    private fun buildDiscordMessage(event: ILoggingEvent): String {
        val timestamp = DateFormatter.DATETIME_FORMATTER.format(Instant.ofEpochMilli(event.timeStamp))
        val requestId = event.mdcPropertyMap["requestId"] ?: "no-id"
        val userId = event.mdcPropertyMap["userId"] ?: "guest"
        val loggerName = event.loggerName.substringAfterLast(".")
        val message = event.formattedMessage.take(MAX_MESSAGE_LENGTH)
        val stackTrace = formatStackTrace(event.throwableProxy)

        val embed = buildString {
            append("""{"embeds":[{""")
            append(""""title":"🚨 ERROR 발생","color":16711680,"fields":[""")

            append("""{"name":"📌 환경","value":"${StringUtil.escapeJson(applicationName)}","inline":true},""")
            append("""{"name":"⏰ 시간","value":"${StringUtil.escapeJson(timestamp)}","inline":true},""")
            append("""{"name":"🆔 RequestId","value":"${StringUtil.escapeJson(requestId)}","inline":true},""")

            append("""{"name":"👤 User","value":"${StringUtil.escapeJson(userId)}","inline":true},""")
            append("""{"name":"📂 Logger","value":"${StringUtil.escapeJson(loggerName)}","inline":true},""")
            append("""{"name":"\u200B","value":"\u200B","inline":true},""")

            append("""{"name":"📝 메시지","value":"```${StringUtil.escapeJson(message)}```","inline":false}""")
            if (stackTrace.isNotBlank()) {
                append(""",{"name":"🔥 StackTrace","value":"```${StringUtil.escapeJson(stackTrace)}```","inline":false}""")
            }
            append("""]}]}""")
        }

        return embed
    }

    private fun formatStackTrace(throwableProxy: IThrowableProxy?): String {
        if (throwableProxy == null) return ""

        return buildString {
            append("${throwableProxy.className}: ${throwableProxy.message}\n")
            throwableProxy.stackTraceElementProxyArray
                ?.take(STACKTRACE_LINES)
                ?.forEach { append("  at ${it.steAsString}\n") }
        }.take(MAX_STACKTRACE_LENGTH)
    }

    private fun sendToDiscord(jsonPayload: String) {
        val url = webhookUrl ?: return

        val request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Content-Type", "application/json; charset=utf-8")
            .POST(HttpRequest.BodyPublishers.ofString(jsonPayload, Charsets.UTF_8))
            .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
            .build()

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .whenComplete { response, throwable ->
                if (throwable != null) {
                    addError("Failed to send Discord notification", throwable)
                } else if (response.statusCode() >= 400) {
                    addError("Discord notification failed with status ${response.statusCode()}")
                }
            }
    }

    private val httpClient: HttpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(TIMEOUT_SECONDS))
        .build()
}
