package application.placeAccessibility

import org.koin.dsl.module

val placeAccessibilityApplicationModule = module {
    single { PlaceAccessibilityApplicationService(get(), get(), get(), get(), get(), get()) }
}
