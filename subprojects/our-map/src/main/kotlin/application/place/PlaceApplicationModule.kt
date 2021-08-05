package application.place

import org.koin.dsl.module

val placeApplicationModule = module {
    single { PlaceApplicationService(get(), get(), get(), get(), get()) }
}
