package com.ilgijjan.common.utils

import java.time.ZoneId
import java.time.format.DateTimeFormatter

object DateFormatter {
    val DOT_DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")

    val DATETIME_FORMATTER: DateTimeFormatter = DateTimeFormatter
        .ofPattern("yyyy-MM-dd HH:mm:ss")
        .withZone(ZoneId.of("Asia/Seoul"))
}
