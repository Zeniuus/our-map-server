package domain.badge

import domain.badge.repository.BadgeRepository
import domain.badge.repository.InMemoryBadgeRepository
import domain.badge.service.UserBadgeIssueService
import org.koin.dsl.module

val badgeDomainModule = module {
    single<BadgeRepository> { InMemoryBadgeRepository() }
    single { UserBadgeIssueService(get(), get(), get(), get()) }
}
