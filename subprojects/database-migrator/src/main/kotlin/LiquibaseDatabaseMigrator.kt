import infra.persistence.configuration.DatabaseConfiguration
import liquibase.Contexts
import liquibase.LabelExpression
import liquibase.Liquibase
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor

object LiquibaseDatabaseMigrator {
    private val liquibase = run {
        val dataSource = DatabaseConfiguration.getDataSource()
        val database = DatabaseFactory.getInstance()
            .findCorrectDatabaseImplementation(JdbcConnection(dataSource.connection))
        Liquibase("liquibase/dbchangelog.xml", ClassLoaderResourceAccessor(), database)
    }

    fun migrate() {
        liquibase.update(Contexts(), LabelExpression())
    }

    fun dropAndMigrate() {
        liquibase.dropAll()
        migrate()
    }
}
