package com.ilgijjan.common.exception

class CustomException(val errorCode: ErrorCode) : RuntimeException(errorCode.message)
