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
        if (query.toLowerCase().contains(Regex("update|delete|drop|alter|insert"))) {
            throw BadRequestException("SELECT 문만 입력 가능합니다.") // TODO: 테스트 작성
        }
        val connection = DatabaseConfiguration.getDataSource().connection
        return try {
            connection.use {
                // 알 수 없는 이유로 collation_connection이 'utf8mb4_general_ci로 고정되어,
                // 데이터 분석 시 변수를 사용할 때 collation 차이로 쿼리가 실행되지 않는 문제가 있다.
                // 이를 해결하기 위해 collation_connection을 강제로 utf8mb4_unicode_ci로 설정해준다.
                connection.createStatement().use { stmt ->
                    stmt.executeQuery("SET collation_connection = 'utf8mb4_unicode_ci'").use {}
                }
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
