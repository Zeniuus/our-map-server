package infra.persistence.configuration

import domain.user.entity.User
import org.hibernate.dialect.MySQL57Dialect
import org.hibernate.jpa.HibernatePersistenceProvider
import java.net.URL
import java.util.Properties
import javax.persistence.EntityManagerFactory
import javax.persistence.SharedCacheMode
import javax.persistence.ValidationMode
import javax.persistence.spi.ClassTransformer
import javax.persistence.spi.PersistenceUnitInfo
import javax.persistence.spi.PersistenceUnitTransactionType
import javax.sql.DataSource

object HibernateJpaConfiguration {
    /**
     * https://docs.jboss.org/hibernate/orm/5.4/userguide/html_single/Hibernate_User_Guide.html#bootstrap-jpa-compliant-EntityManagerFactory-example
     */
    fun createEntityManagerFactory(): EntityManagerFactory {
        return HibernatePersistenceProvider().createContainerEntityManagerFactory(
            getPersistenceUnitInfo(),
            emptyMap<String, Any>()
        )
    }

    /**
     * https://vladmihalcea.com/how-to-bootstrap-jpa-programmatically-without-the-persistence-xml-configuration-file/
     */
    private fun getPersistenceUnitInfo() = object : PersistenceUnitInfo {
        override fun getPersistenceUnitName(): String {
            return "our-map"
        }

        override fun getPersistenceProviderClassName(): String {
            return HibernatePersistenceProvider::javaClass.name
        }

        override fun getTransactionType(): PersistenceUnitTransactionType {
            return PersistenceUnitTransactionType.RESOURCE_LOCAL
        }

        override fun getJtaDataSource(): DataSource? {
            return null
        }

        override fun getNonJtaDataSource(): DataSource {
            return DatabaseConfiguration.getDataSource()
        }

        override fun getMappingFileNames(): MutableList<String> {
            return mutableListOf()
        }

        override fun getJarFileUrls(): MutableList<URL> {
            return mutableListOf()
        }

        override fun getPersistenceUnitRootUrl(): URL? {
            return null
        }

        override fun getManagedClassNames(): MutableList<String> {
            return mutableListOf(User::class.qualifiedName!!) // TODO
        }

        override fun excludeUnlistedClasses(): Boolean {
            return false
        }

        override fun getSharedCacheMode(): SharedCacheMode {
            return SharedCacheMode.UNSPECIFIED
        }

        override fun getValidationMode(): ValidationMode {
            return ValidationMode.AUTO
        }

        /**
         * https://docs.jboss.org/hibernate/orm/5.4/userguide/html_single/Hibernate_User_Guide.html#configurations
         */
        override fun getProperties(): Properties {
            // TODO: 설정 파일로 빼기
            return Properties().apply {
                setProperty("hibernate.dialect", MySQL57Dialect::class.qualifiedName!!)
                setProperty("hibernate.connection.provider_disables_autocommit", "true")
                setProperty("hibernate.show_sql", "true")
                setProperty("hibernate.hbm2ddl.auto", "create-drop") // TODO: 테스트를 위한 임시 코드; liquibase로 갈아타기
                setProperty("hibernate.physical_naming_strategy", SnakeCasePhysicalNamingStrategy::class.qualifiedName!!)
            }
        }

        override fun getPersistenceXMLSchemaVersion(): String {
            return "2.1"
        }

        override fun getClassLoader(): ClassLoader {
            return Thread.currentThread().contextClassLoader
        }

        override fun addTransformer(transformer: ClassTransformer?) {
        }

        override fun getNewTempClassLoader(): ClassLoader? {
            return null
        }
    }
}
