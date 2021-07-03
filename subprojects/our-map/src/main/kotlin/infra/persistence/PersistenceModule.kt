package infra.persistence

import domain.user.repository.UserRepository
import infra.persistence.configuration.HibernateJpaConfiguration
import infra.persistence.repository.GlobalEntityManagerHolder
import infra.persistence.repository.JpaUserRepository
import org.koin.dsl.module

val persistenceModule = module {
    single { HibernateJpaConfiguration.createEntityManagerFactory() }
    single { GlobalEntityManagerHolder.getInstance(get()) }
    single<UserRepository> { JpaUserRepository(get()) }
}
