package domain.placeAccessibility

import domain.placeAccessibility.service.BuildingAccessibilityService
import domain.placeAccessibility.service.PlaceAccessibilityEventPublisher
import domain.placeAccessibility.service.PlaceAccessibilityService
import domain.placeAccessibility.service.SearchPlaceAccessibilityService
import org.koin.dsl.module

val placeAccessibilityDomainModule = module {
    single { SearchPlaceAccessibilityService(get(), get()) }
    single { PlaceAccessibilityService(get(), get()) }
    single { BuildingAccessibilityService(get()) }
    single { PlaceAccessibilityEventPublisher(get(), get(), get()) }
}
