package infra.monitoring

import infra.properties.OurMapProperties
import io.sentry.Sentry
import org.slf4j.LoggerFactory

object ErrorReporter {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val isSentryEnabled: Boolean

    init {
        val sentryDsn = OurMapProperties["sentry.dsn"]
        isSentryEnabled = sentryDsn != null

        if (isSentryEnabled) {
            Sentry.init { options ->
                options.dsn = sentryDsn
            }
        }
    }

    fun report(t: Throwable) {
        if (isSentryEnabled) {
            Sentry.captureException(t)
        } else {
            logger.error(t.message, t)
        }
    }
}
