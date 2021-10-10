package quest.application

import org.koin.dsl.module

val clubQuestApplicationModule = module {
    single { ClubQuestApplicationService(get(), get(), get()) }
}
