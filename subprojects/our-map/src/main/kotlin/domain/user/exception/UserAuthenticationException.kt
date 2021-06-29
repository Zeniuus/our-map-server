package domain.user.exception

class UserAuthenticationException(
    val errorCode: ErrorCode
) : RuntimeException() {
    companion object {
        val USER_DOES_NOT_EXIST = UserAuthenticationException(ErrorCode.USER_DOES_NOT_EXIST)
        val WRONG_PASSWORD = UserAuthenticationException(ErrorCode.WRONG_PASSWORD)
    }

    enum class ErrorCode {
        USER_DOES_NOT_EXIST,
        WRONG_PASSWORD,
    }
}
