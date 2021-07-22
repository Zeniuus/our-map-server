package auth

import org.koin.dsl.module

val ourMapAuthModule = module {
    single { UserAuthenticator(get()) }
}
