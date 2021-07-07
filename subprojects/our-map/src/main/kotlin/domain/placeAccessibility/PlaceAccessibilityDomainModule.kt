package domain.placeAccessibility

import domain.placeAccessibility.service.BuildingAccessibilityService
import domain.placeAccessibility.service.PlaceAccessibilityService
import domain.placeAccessibility.service.SearchPlaceAccessibilityService
import org.koin.dsl.module

val placeAccessibilityDomainModule = module {
    single { SearchPlaceAccessibilityService(get(), get()) }
    single { PlaceAccessibilityService(get()) }
    single { BuildingAccessibilityService(get()) }
}
