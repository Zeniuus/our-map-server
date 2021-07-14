package infra.persistence.configuration

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import infra.properties.OurMapProperties

object DatabaseConfiguration {
    val dataSource = run {
        // TODO: 설정 파일로 빼기
        val config = HikariConfig()
        config.jdbcUrl = OurMapProperties["hikari.jdbcUrl"]
        config.username = OurMapProperties["hikari.username"]
        config.password = OurMapProperties["hikari.password"]
        config.isAutoCommit = false // TODO: auto-commit 옵션 공부
        HikariDataSource(config)
    }
}
