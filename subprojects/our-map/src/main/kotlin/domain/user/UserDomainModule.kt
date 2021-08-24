package domain.user

import domain.user.service.UserAuthService
import domain.user.service.UserService
import org.koin.dsl.module

val userDomainModule = module {
    single { UserService(get(), get()) }
    single { UserAuthService(get(), get()) }
}
