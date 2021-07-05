package domain.place

import domain.place.service.SearchPlaceService
import org.koin.dsl.module

val placeDomainModule = module {
    single { SearchPlaceService(get()) }
}
