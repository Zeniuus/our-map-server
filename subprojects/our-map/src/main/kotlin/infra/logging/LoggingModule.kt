package infra.logging

import domain.logging.OurMapEventLogger
import infra.logging.jpa.JpaOurMapEventEntityRepository
import infra.logging.jpa.JpaOurMapEventLogger
import infra.logging.jpa.OurMapEventEntityRepository
import org.koin.dsl.module

val loggingModule = module {
    single<OurMapEventEntityRepository> { JpaOurMapEventEntityRepository() }
    single<OurMapEventLogger> { JpaOurMapEventLogger(get()) }
}
