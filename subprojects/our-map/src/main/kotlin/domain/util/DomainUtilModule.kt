package domain.util

import org.koin.dsl.module

val domainUtilModule = module {
    single { JWT("secret", get()) } // TODO: secret을 properties 파일로 구성하기
}
