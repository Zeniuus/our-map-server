package util

import org.koin.dsl.module

val utilModule = module {
    single { RunSqlService() }
}
