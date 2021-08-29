package application.accessibility

import org.koin.dsl.module

val accessibilityApplicationModule = module {
    single { AccessibilityApplicationService(get(), get(), get(), get(), get(), get(), get()) }
    single { BuildingAccessibilityUpvoteApplicationService(get(), get(), get(), get()) }
    single { AccessibilityRegisteredEventHandler(get(), get(), get()) }
}
