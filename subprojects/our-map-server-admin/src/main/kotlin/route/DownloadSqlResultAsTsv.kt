package route

import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.response.header
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.post
import org.koin.core.context.GlobalContext
import util.RunSqlService
import java.time.Clock
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun Route.downloadSqlResultAsTsv() {
    val koin = GlobalContext.get()
    val clock = koin.get<Clock>()
    val runSqlService = koin.get<RunSqlService>()

    post("/api/downloadSqlResultAsTsv") {
        val params = call.receive<RunSqlParams>()

        val result = runSqlService.runSelectQuery(params.query)
        val lines = listOf(result.columns) + result.rows
        val nowStr = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss").format(clock.instant().atZone(ZoneId.of("Asia/Seoul")))
        call.response.header("Content-Disposition", "attachment; filename=\"$nowStr.tsv\"")
        call.respondText(
            lines.joinToString("\n") { columns ->
                columns.joinToString("\t") { column ->
                    column ?: ""
                }
            }
        )
    }
}
