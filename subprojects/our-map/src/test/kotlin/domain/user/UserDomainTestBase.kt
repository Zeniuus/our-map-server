package domain.user

open class UserDomainTestBase {
    protected val koin = OurMapIoCFactory.createScopedContainer {
        modules(
            userDomainModule,
        )
    }
}
