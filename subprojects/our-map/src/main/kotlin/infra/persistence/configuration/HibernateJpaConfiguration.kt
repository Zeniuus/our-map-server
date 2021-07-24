package infra.persistence.configuration

import domain.place.entity.Building
import domain.place.entity.Place
import domain.placeAccessibility.entity.BuildingAccessibility
import domain.placeAccessibility.entity.PlaceAccessibility
import domain.user.entity.User
import domain.village.entity.Village
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

    private var entityManagerFactory: EntityManagerFactory? = null
    /**
     * https://docs.jboss.org/hibernate/orm/5.4/userguide/html_single/Hibernate_User_Guide.html#bootstrap-jpa-compliant-EntityManagerFactory-example
     */
    @JvmStatic
    fun createEntityManagerFactory(): EntityManagerFactory {
        if (entityManagerFactory == null) {
            entityManagerFactory = HibernatePersistenceProvider().createContainerEntityManagerFactory(
                getPersistenceUnitInfo(),
                emptyMap<String, Any>()
            )
        }
        return entityManagerFactory!!
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
            // TODO: 자동으로 등록
            return mutableListOf(
                User::class.qualifiedName!!,
                Place::class.qualifiedName!!,
                Building::class.qualifiedName!!,
                PlaceAccessibility::class.qualifiedName!!,
                BuildingAccessibility::class.qualifiedName!!,
                Village::class.qualifiedName!!,
            )
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
            return Properties().apply {
                setProperty("hibernate.dialect", MySQL57Dialect::class.qualifiedName!!)
                setProperty("hibernate.connection.provider_disables_autocommit", "true")
                setProperty("hibernate.show_sql", "true")
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
