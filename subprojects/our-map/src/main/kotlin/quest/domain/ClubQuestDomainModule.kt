package quest.domain

import org.koin.dsl.module
import quest.domain.service.ClubQuestService

val clubQuestDomainModule = module {
    single { ClubQuestService(get(), get()) }
}
