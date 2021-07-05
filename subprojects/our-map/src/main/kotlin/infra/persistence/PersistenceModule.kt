package infra.persistence

import domain.place.repository.BuildingRepository
import domain.place.repository.PlaceRepository
import domain.placeAccessibility.repository.BuildingAccessibilityRepository
import domain.placeAccessibility.repository.PlaceAccessibilityRepository
import domain.user.repository.UserRepository
import infra.persistence.configuration.HibernateJpaConfiguration
import infra.persistence.repository.GlobalEntityManagerHolder
import infra.persistence.repository.JpaBuildingAccessibililtyRepository
import infra.persistence.repository.JpaBuildingRepository
import infra.persistence.repository.JpaPlaceAccessibilityRepository
import infra.persistence.repository.JpaPlaceRepository
import infra.persistence.repository.JpaUserRepository
import org.koin.dsl.module

val persistenceModule = module {
    single { HibernateJpaConfiguration.createEntityManagerFactory() }
    single { GlobalEntityManagerHolder.getInstance(get()) }

    single<UserRepository> { JpaUserRepository(get()) }
    single<PlaceRepository> { JpaPlaceRepository(get()) }
    single<BuildingRepository> { JpaBuildingRepository(get()) }
    single<PlaceAccessibilityRepository> { JpaPlaceAccessibilityRepository(get()) }
    single<BuildingAccessibilityRepository> { JpaBuildingAccessibililtyRepository(get()) }
}
