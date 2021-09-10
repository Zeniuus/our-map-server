package util

import infra.persistence.configuration.DatabaseConfiguration

class RunSqlService {
    data class Result(
        val columns: List<String>,
        val rows: List<List<String?>>,
    )

    fun runSql(query: String): Result {
        val connection = DatabaseConfiguration.getDataSource().connection
        val stmt = connection.createStatement()
        return stmt.executeQuery(query).use { resultSet ->
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
