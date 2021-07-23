import domain.util.domainUtilModule
import domain.village.repository.EupMyeonDongRepository
import domain.village.repository.InMemoryEupMyeonDongRepository
import domain.village.repository.InMemorySiGunGuRepository
import domain.village.repository.SiGunGuRepository
import infra.persistence.persistenceModule
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.error.KoinAppAlreadyStartedException
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import java.time.Clock

object OurMapIoCFactory {
    @Synchronized
    fun configGlobally(customConfigBlock: KoinApplication.() -> KoinApplication) = try {
        startKoin {
            installCommonConfig()
            customConfigBlock()
        }
    } catch (e: KoinAppAlreadyStartedException) {
        GlobalContext.getKoinApplicationOrNull()!!
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
                single<EupMyeonDongRepository> { InMemoryEupMyeonDongRepository() }
                single<SiGunGuRepository> { InMemorySiGunGuRepository() }
            }
        )
    }
}
