package infra.persistence

import application.TransactionManager
import domain.place.repository.BuildingRepository
import domain.place.repository.PlaceRepository
import domain.placeAccessibility.repository.BuildingAccessibilityRepository
import domain.placeAccessibility.repository.BuildingAccessibilityUpvoteRepository
import domain.placeAccessibility.repository.PlaceAccessibilityRepository
import domain.user.repository.UserRepository
import domain.village.repository.UserFavoriteVillageRepository
import domain.village.repository.VillageRepository
import infra.persistence.configuration.HibernateJpaConfiguration
import infra.persistence.repository.JpaBuildingAccessibilityRepository
import infra.persistence.repository.JpaBuildingAccessibilityUpvoteRepository
import infra.persistence.repository.JpaBuildingRepository
import infra.persistence.repository.JpaPlaceAccessibilityRepository
import infra.persistence.repository.JpaPlaceRepository
import infra.persistence.repository.JpaUserFavoriteVillageRepository
import infra.persistence.repository.JpaUserRepository
import infra.persistence.repository.JpaVillageRepository
import infra.persistence.transaction.JpaTransactionManager
import org.koin.dsl.bind
import org.koin.dsl.module

val persistenceModule = module {
    single { HibernateJpaConfiguration.createEntityManagerFactory() }

    single<UserRepository> { JpaUserRepository() }
    single<PlaceRepository> { JpaPlaceRepository() }
    single<BuildingRepository> { JpaBuildingRepository() }
    single<PlaceAccessibilityRepository> { JpaPlaceAccessibilityRepository() }
    single<BuildingAccessibilityRepository> { JpaBuildingAccessibilityRepository() }
    single<BuildingAccessibilityUpvoteRepository> { JpaBuildingAccessibilityUpvoteRepository() }
    single<VillageRepository> { JpaVillageRepository() }
    single<UserFavoriteVillageRepository> { JpaUserFavoriteVillageRepository() }
    single { JpaTransactionManager(get()) } bind TransactionManager::class
}
