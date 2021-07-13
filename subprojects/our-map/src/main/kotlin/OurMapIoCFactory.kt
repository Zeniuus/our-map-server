import domain.util.domainUtilModule
import infra.persistence.persistenceModule
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import java.time.Clock

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
            domainUtilModule,
            persistenceModule,
        )
        modules(
            module {
                single { Clock.systemUTC() }
            }
        )
    }
}
