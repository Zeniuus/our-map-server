package domain.placeAccessibility

open class PlaceAccessibilityDomainTestBase {
    protected val koin = OurMapIoCFactory.createScopedContainer {
        modules(
            placeAccessibilityDomainModule,
        )
    }
}
