package infra.persistence.configuration

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import javax.sql.DataSource

object DatabaseConfiguration {
    fun getDataSource(): DataSource {
        // TODO: 설정 파일로 빼기
        val config = HikariConfig()
        config.jdbcUrl = "jdbc:mysql://localhost:3306/our_map?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&allowMultiQueries=true&useSSL=false"
        config.username = "root"
        config.password = ""
        config.isAutoCommit = false // TODO: auto-commit 옵션 공부
        return HikariDataSource(config)
    }
}
