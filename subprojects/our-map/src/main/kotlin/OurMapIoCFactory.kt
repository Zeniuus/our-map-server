import infra.persistence.persistenceModule
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.koinApplication

object OurMapIoCFactory {
    fun configGlobally(customConfigBlock: KoinApplication.() -> KoinApplication) = startKoin {
        installCommonConfig()
        customConfigBlock()
    }

    fun createScopedContainer(customConfigBlock: KoinApplication.() -> KoinApplication): Koin = koinApplication {
        installCommonConfig()
        customConfigBlock()
    }.koin

    fun KoinApplication.installCommonConfig() {
        modules(
            persistenceModule,
        )
    }
}
