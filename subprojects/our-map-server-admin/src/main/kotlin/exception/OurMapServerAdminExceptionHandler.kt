package exception

import BadRequestException
import infra.monitoring.ErrorReporter
import io.ktor.http.HttpStatusCode

object OurMapServerAdminExceptionHandler {
    data class Result(
        val statusCode: HttpStatusCode,
        val body: Body,
    ) {
        data class Body(
            val msg: String,
        )
    }

    fun handle(t: Throwable): Result {
        return when (t) {
            is BadRequestException -> handleBadRequestException(t)
            else -> handleUnexpectedError(t)
        }
    }

    private fun handleBadRequestException(e: BadRequestException): Result {
        return Result(
            statusCode = HttpStatusCode.BadRequest,
            body = Result.Body(msg = e.msg),
        )
    }

    private fun handleUnexpectedError(t: Throwable): Result {
        ErrorReporter.report(t)
        return Result(
            statusCode = HttpStatusCode.InternalServerError,
            body = Result.Body(msg = "알 수 없는 문제가 발생했습니다."),
        )
    }
}
