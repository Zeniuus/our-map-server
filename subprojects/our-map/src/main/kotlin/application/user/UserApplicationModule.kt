package application.user

import org.koin.dsl.module

val userApplicationModule = module {
    single { UserApplicationService(get()) }
}
