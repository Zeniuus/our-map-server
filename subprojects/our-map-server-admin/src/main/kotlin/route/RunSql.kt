package route

import application.TransactionManager
import infra.persistence.configuration.DatabaseConfiguration
import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import org.koin.core.context.GlobalContext

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
    val transactionManager = koin.get<TransactionManager>()

    post("/api/runSql") {
        val params = call.receive<RunSqlParams>()

        val connection = DatabaseConfiguration.getDataSource().connection
        val stmt = connection.createStatement()
        val result = stmt.executeQuery(params.query).use { resultSet ->
            val resultSetMetadata = resultSet.metaData
            val columns = (1..resultSetMetadata.columnCount).map { idx ->
                resultSetMetadata.getColumnName(idx)
            }
            val rows = mutableListOf<List<String>>()
            while (resultSet.next()) {
                rows.add(columns.map { resultSet.getString(it) })
            }
            RunSqlResult(
                columns = columns,
                rows = rows,
            )
        }

        call.respond(result)
    }
}
