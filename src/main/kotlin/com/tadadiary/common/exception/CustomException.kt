class CustomException(val errorCode: ErrorCode) : RuntimeException(errorCode.message)
