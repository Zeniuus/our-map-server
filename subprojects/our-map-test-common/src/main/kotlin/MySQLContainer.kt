import infra.persistence.configuration.DatabaseConfiguration
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName

object MySQLContainer {
    private var mysql: GenericContainer<Nothing>? = null

    @Synchronized
    fun startOnce() {
        if (mysql == null) {
            try {
                mysql = GenericContainer<Nothing>(DockerImageName.parse("mysql:5.7.34"))
                    .apply {
                        withExposedPorts(3306)
                        withEnv("MYSQL_ROOT_PASSWORD", "password")
                    }
                mysql!!.start()

                DatabaseConfiguration.setOverridingProperties(
                    jdbcUrl = "jdbc:mysql://localhost:${mysql!!.firstMappedPort}/our_map?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&allowMultiQueries=true&useSSL=false&createDatabaseIfNotExist=true",
                    password = "password",
                )
                LiquibaseDatabaseMigrator.migrate()
            } catch (t: Throwable) {
                mysql = null
                throw t
            }
        }
    }
}
