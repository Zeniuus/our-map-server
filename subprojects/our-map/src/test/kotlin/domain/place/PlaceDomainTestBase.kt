package domain.place

open class PlaceDomainTestBase {
    protected val koin = OurMapIoCFactory.createScopedContainer {
        modules(
            placeDomainModule,
        )
    }
}
