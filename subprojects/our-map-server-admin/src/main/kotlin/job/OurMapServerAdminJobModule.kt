package job

import org.koin.dsl.module

val ourMapServerAdminJobModule = module {
    single { OurMapServerAdminJobRunner(get()) }
    single { EtlClubQuestResultJob(get(), get(), get()) }
}
