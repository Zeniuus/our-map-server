package domain.user

import org.koin.dsl.koinApplication

open class UserDomainTestBase {
    protected val koin = koinApplication {
        modules(userDomainModule)
    }.koin
}
