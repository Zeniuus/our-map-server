package job

import org.koin.dsl.module

val ourMapServerAdminJobModule = module {
    single { OurMapServerAdminJobRunner(get(), get()) }
    single { EtlClubQuestResultJob(get(), get(), get()) }
    single { EtlClosedStoreJob(get()) }
}
