import infra.persistence.configuration.DatabaseConfiguration
import liquibase.Contexts
import liquibase.LabelExpression
import liquibase.Liquibase
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor

/**
 * LiquibaseDatabaseMigrator를 our-map 프로젝트에 포함시키면, our-map 프로젝트의 테스트를 돌릴 때
 * liquibase/dbchangelog.xml이 두 개 있다고 에러가 난다. 하나는 build/libs/our-map.jar 파일 안에서
 * 발견되고, 다른 하나는 build/resources/main 폴더 안에서 발견된다.
 *
 * 이 문제를 방지하기 위해 database-migrator 프로젝트를 따로 판다. 이렇게 하면 our-map 프로젝트의
 * 테스트를 실행할 때 build/resources/main에 dbchangelog.xml 파일이 복사되어 들어가지 않으므로
 * 위와 같은 오류를 피할 수 있다.
 */
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
