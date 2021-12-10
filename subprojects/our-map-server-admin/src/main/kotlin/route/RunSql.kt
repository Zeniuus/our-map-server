package route

import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import org.koin.core.context.GlobalContext
import util.RunSqlService

// TODO: protobuf로 처리하기
data class RunSqlParams(
    val query: String,
)

data class RunSqlResult(
    val columns: List<String>,
    val rows: List<List<String>>,
)

fun Route.runSql() {
    val koin = GlobalContext.get()
    val runSqlService = koin.get<RunSqlService>()

    post("/api/runSql") {
        val params = call.receive<RunSqlParams>()

        val result = runSqlService.runSelectQuery(params.query)
        call.respond(RunSqlResult(
            columns = result.columns,
            rows = result.rows.map { row -> row.map { it ?: "" } },
        ))
    }
}
