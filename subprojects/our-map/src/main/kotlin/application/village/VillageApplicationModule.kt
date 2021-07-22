package application.village

import org.koin.dsl.module

val villageApplicationModule = module {
    single { VillageApplicationService(get(), get()) }
}
