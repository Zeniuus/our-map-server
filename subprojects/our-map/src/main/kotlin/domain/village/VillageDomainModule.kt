package domain.village

import domain.village.repository.EupMyeonDongRepository
import domain.village.repository.InMemoryEupMyeonDongRepository
import domain.village.repository.InMemorySiGunGuRepository
import domain.village.repository.SiGunGuRepository
import domain.village.service.VillageService
import org.koin.dsl.module

val villageDomainModule = module {
    single<SiGunGuRepository> { InMemorySiGunGuRepository() }
    single<EupMyeonDongRepository> { InMemoryEupMyeonDongRepository() }
    single { VillageService(get(), get(), get(), get(), get()) }
}
