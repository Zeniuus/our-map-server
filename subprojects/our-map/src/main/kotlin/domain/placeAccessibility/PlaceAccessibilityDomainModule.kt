package domain.placeAccessibility

import domain.placeAccessibility.service.SearchPlaceAccessibilityService
import org.koin.dsl.module

val placeAccessibilityDomainModule = module {
    single { SearchPlaceAccessibilityService(get(), get()) }
}
