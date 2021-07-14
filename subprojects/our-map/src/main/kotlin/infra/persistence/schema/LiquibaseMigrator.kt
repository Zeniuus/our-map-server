package infra.persistence.schema

import infra.persistence.configuration.DatabaseConfiguration
import infra.properties.OurMapProperties
import liquibase.CatalogAndSchema
import liquibase.Contexts
import liquibase.LabelExpression
import liquibase.Liquibase
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor

object LiquibaseMigrator {
    private val liquibase = run {
        val dataSource = DatabaseConfiguration.dataSource
        val database = DatabaseFactory.getInstance()
            .findCorrectDatabaseImplementation(JdbcConnection(dataSource.connection))
        Liquibase("classpath://liquibase/dbchangelog.xml", ClassLoaderResourceAccessor(), database)
    }

    fun migrate() {
        liquibase.update(Contexts(), LabelExpression())
    }

    fun dropAndMigrate() {
        liquibase.dropAll()
        migrate()
    }
}
