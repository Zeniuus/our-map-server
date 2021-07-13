package domain.user.exception

class UserAuthenticationException(
    val errorCode: ErrorCode
) : RuntimeException() {
    enum class ErrorCode {
        USER_DOES_NOT_EXIST,
        WRONG_PASSWORD,
        INVALID_ACCESS_TOKEN,
    }
}
