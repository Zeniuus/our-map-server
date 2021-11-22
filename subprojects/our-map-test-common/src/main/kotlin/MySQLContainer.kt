import infra.persistence.configuration.DatabaseConfiguration
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName

object MySQLContainer {
    private var mysql: GenericContainer<Nothing>? = null

    @Synchronized
    fun startOnce() {
        if (mysql == null) {
            try {
                val password = "password"
                // MySQL 도커 이미지 중 apple silicon을 지원하는 게 없어서 MariaDB로 대체한다.
                // refs: https://docs.docker.com/desktop/mac/apple-silicon/#known-issues
                mysql = GenericContainer<Nothing>(DockerImageName.parse("mariadb:10.6"))
                    .apply {
                        withExposedPorts(3306)
                        withEnv("MYSQL_ROOT_PASSWORD", password)
                        // https://devs0n.tistory.com/24
                        // character_set_server와 collation_server를 지정하지 않으면
                        // 테이블이 잘못된 charset / collation으로 만들어진다.
                        withCommand(
                            "mysqld",
                            "--character-set-server=utf8mb4",
                            "--collation-server=utf8mb4_unicode_ci",
                        )
                    }
                mysql!!.start() // https://www.testcontainers.org/test_framework_integration/manual_lifecycle_control/

                DatabaseConfiguration.setOverridingProperties(
                    jdbcUrl = "jdbc:mysql://localhost:${mysql!!.firstMappedPort}/our_map?autoReconnect=true&allowMultiQueries=true&useSSL=false&createDatabaseIfNotExist=true",
                    password = password,
                )
                LiquibaseDatabaseMigrator.migrate()
            } catch (t: Throwable) {
                mysql = null
                throw t
            }
        }
    }
}
