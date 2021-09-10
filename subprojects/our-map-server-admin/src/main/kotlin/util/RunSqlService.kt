package util

import BadRequestException
import infra.persistence.configuration.DatabaseConfiguration

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
        return connection.use {
            connection.createStatement().use { stmt ->
                stmt.executeQuery(query).use { resultSet ->
                    val resultSetMetadata = resultSet.metaData
                    val columns = (1..resultSetMetadata.columnCount).map { idx ->
                        resultSetMetadata.getColumnName(idx)
                    }
                    val rows = mutableListOf<List<String>>()
                    while (resultSet.next()) {
                        rows.add(columns.map { resultSet.getString(it) })
                    }
                    Result(
                        columns = columns,
                        rows = rows,
                    )
                }
            }
        }
    }
}
