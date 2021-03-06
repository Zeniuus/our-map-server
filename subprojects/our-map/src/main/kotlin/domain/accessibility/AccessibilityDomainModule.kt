package domain.accessibility

import domain.accessibility.service.BuildingAccessibilityCommentService
import domain.accessibility.service.BuildingAccessibilityService
import domain.accessibility.service.BuildingAccessibilityUpvoteService
import domain.accessibility.service.ConquerRankingService
import domain.accessibility.service.PlaceAccessibilityCommentService
import domain.accessibility.service.PlaceAccessibilityService
import domain.accessibility.service.SearchAccessibilityService
import org.koin.dsl.module

val accessibilityDomainModule = module {
    single { SearchAccessibilityService(get(), get()) }
    single { PlaceAccessibilityService(get(), get(), get()) }
    single { PlaceAccessibilityCommentService(get(), get(), get()) }
    single { BuildingAccessibilityService(get(), get()) }
    single { BuildingAccessibilityCommentService(get(), get(), get()) }
    single { BuildingAccessibilityUpvoteService(get(), get()) }
    single { ConquerRankingService(get()) }
}
