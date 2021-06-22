package domain.user

import domain.user.repository.InMemoryUserRepository
import domain.user.repository.UserRepository
import domain.user.service.UserService
import org.koin.dsl.module

val userDomainModule = module {
    single { UserService(get()) }
    single<UserRepository> { InMemoryUserRepository() }
}
