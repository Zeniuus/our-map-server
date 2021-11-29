package job

class OurMapServerAdminJobRunner(
    private val etlClubQuestResultJob: EtlClubQuestResultJob,
) {
    fun startJobs() {
        etlClubQuestResultJob.start()
    }
}
