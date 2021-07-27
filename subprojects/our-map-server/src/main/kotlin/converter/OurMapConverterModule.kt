package converter

import org.koin.dsl.module

val ourMapConverterModule = module {
    single { BuildingAccessibilityConverter(get()) }
    single { PlaceAccessibilityConverter(get()) }
    single { VillageConverter(get()) }
}
