package util

import BadRequestException
import infra.persistence.configuration.DatabaseConfiguration
import java.sql.SQLException

class RunSqlService {
    data class Result(
        val columns: List<String>,
        val rows: List<List<String?>>,
    )

    fun runSql(query: String): Result {
        if (query.toLowerCase().contains(Regex("update|delete|drop|alter"))) {
            throw BadRequestException("SELECT 문만 입력 가능합니다.") // TODO: 테스트 작성
        }
        val connection = DatabaseConfiguration.getDataSource().connection
        return try {
            connection.use {
                connection.createStatement().use { stmt ->
                    stmt.executeQuery(query).use { resultSet ->
                        val resultSetMetadata = resultSet.metaData
                        val columns = (1..resultSetMetadata.columnCount).map { idx ->
                            resultSetMetadata.getColumnName(idx)
                        }
                        val rows = mutableListOf<List<String>>()
                        while (resultSet.next()) {
                            rows.add(columns.mapIndexed { idx, _ -> resultSet.getString(idx + 1) })
                        }
                        Result(
                            columns = columns,
                            rows = rows,
                        )
                    }
                }
            }
        } catch (e: SQLException) {
            throw BadRequestException("잘못된 쿼리입니다. (원인: ${e.message})")
        }
    }
}
