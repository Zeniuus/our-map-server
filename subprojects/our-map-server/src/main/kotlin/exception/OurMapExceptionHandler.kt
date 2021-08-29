package exception

import com.google.protobuf.InvalidProtocolBufferException
import domain.DomainException
import domain.user.exception.UserAuthenticationException
import infra.monitoring.ErrorReporter
import infra.persistence.repository.EntityNotFoundException
import io.ktor.http.HttpStatusCode
import ourMap.protocol.Model

object OurMapExceptionHandler {
    data class Result(
        val statusCode: HttpStatusCode,
        val body: Model.OurMapError,
    )

    fun handle(t: Throwable): Result {
        return when (t) {
            is UserAuthenticationException -> handleUserAuthenticationException(t)
            is DomainException -> handleDomainException(t)
            is EntityNotFoundException -> handleEntityNotFoundException(t)
            is InvalidProtocolBufferException -> handleInvalidProtocolBufferException(t)
            else -> handleUnexpectedError(t)
        }
    }

    private fun handleUserAuthenticationException(e: UserAuthenticationException): Result {
        return Result(
            statusCode = HttpStatusCode.Unauthorized,
            body = Model.OurMapError.newBuilder()
                .setMessage("인증되지 않은 유저입니다.")
                .build(),
        )
    }

    private fun handleDomainException(e: DomainException): Result {
        return Result(
            statusCode = HttpStatusCode.BadRequest,
            body = Model.OurMapError.newBuilder()
                .setMessage(e.msg)
                .build(),
        )
    }

    private fun handleEntityNotFoundException(e: EntityNotFoundException): Result {
        return Result(
            statusCode = HttpStatusCode.BadRequest,
            body = Model.OurMapError.newBuilder()
                .setMessage(e.msg)
                .build(),
        )
    }

    private fun handleInvalidProtocolBufferException(e: InvalidProtocolBufferException): Result {
        return Result(
            statusCode = HttpStatusCode.BadRequest,
            body = Model.OurMapError.newBuilder()
                .setMessage("올바르지 않은 request body 입니다.(${e.message})")
                .build(),
        )
    }

    private fun handleUnexpectedError(t: Throwable): Result {
        ErrorReporter.report(t)
        return Result(
            statusCode = HttpStatusCode.InternalServerError,
            body = Model.OurMapError.newBuilder()
                .setMessage("알 수 없는 문제가 발생했습니다.")
                .build(),
        )
    }
}
