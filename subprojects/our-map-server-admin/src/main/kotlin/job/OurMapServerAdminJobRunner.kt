package job

class OurMapServerAdminJobRunner(
    private val etlClubQuestResultJob: EtlClubQuestResultJob,
    private val etlClosedStoreJob: EtlClosedStoreJob,
) {
    fun startJobs() {
        etlClubQuestResultJob.start()
        etlClosedStoreJob.start()
    }
}
