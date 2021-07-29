package infra.persistence.configuration

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import infra.properties.OurMapProperties
import java.util.Properties
import javax.sql.DataSource

object DatabaseConfiguration {
    private var dataSource: DataSource? = null
    private var overridingProperties: Properties? = null

    fun setOverridingProperties(
        jdbcUrl: String,
        password: String,
    ) {
        this.overridingProperties = Properties().apply {
            setProperty("hikari.jdbcUrl", jdbcUrl)
            setProperty("hikari.password", password)
        }
    }

    @Synchronized
    fun getDataSource(): DataSource {
        if (dataSource == null) {
            val config = HikariConfig()
            config.jdbcUrl = overridingProperties?.get("hikari.jdbcUrl")?.toString() ?: OurMapProperties["hikari.jdbcUrl"]
            config.username = overridingProperties?.get("hikari.username")?.toString() ?: OurMapProperties["hikari.username"]
            config.password = overridingProperties?.get("hikari.password")?.toString() ?: OurMapProperties["hikari.password"]
            config.isAutoCommit = false // TODO: auto-commit 옵션 공부
            dataSource = HikariDataSource(config)
        }
        return dataSource!!
    }
}
