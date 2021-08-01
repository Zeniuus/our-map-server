package domain.accessibility

import domain.accessibility.service.BuildingAccessibilityService
import domain.accessibility.service.BuildingAccessibilityUpvoteService
import domain.accessibility.service.PlaceAccessibilityEventPublisher
import domain.accessibility.service.PlaceAccessibilityService
import domain.accessibility.service.SearchAccessibilityService
import org.koin.dsl.module

val accessibilityDomainModule = module {
    single { SearchAccessibilityService(get(), get()) }
    single { PlaceAccessibilityService(get(), get()) }
    single { BuildingAccessibilityService(get()) }
    single { BuildingAccessibilityUpvoteService(get(), get()) }
    single { PlaceAccessibilityEventPublisher(get(), get(), get()) }
}