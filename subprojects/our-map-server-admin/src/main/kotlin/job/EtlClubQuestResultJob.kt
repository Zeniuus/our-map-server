package job

import application.TransactionManager
import domain.util.EntityIdGenerator
import infra.monitoring.ErrorReporter
import quest.domain.entity.ClubQuestResult
import quest.domain.repository.ClubQuestRepository
import quest.domain.repository.ClubQuestResultRepository
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class EtlClubQuestResultJob(
    private val transactionManager: TransactionManager,
    private val clubQuestRepository: ClubQuestRepository,
    private val clubQuestResultRepository: ClubQuestResultRepository,
) {
    private val executor = Executors.newSingleThreadScheduledExecutor()

    fun start() {
        executor.scheduleAtFixedRate(::run, 0L, 1L, TimeUnit.HOURS)
    }

    fun run() {
        val clubQuests = transactionManager.doInTransaction {
            clubQuestRepository.listNotDeleted()
        }
        clubQuests.forEach { clubQuest ->
            try {
                transactionManager.doInTransaction {
                    val existingResultByPlaceKey = clubQuestResultRepository.findByQuestId(clubQuest.id)
                        .associateBy {
                            ClubQuestPlaceKey(
                                questId = it.questId,
                                displayedName = it.questTargetDisplayedName,
                                placeName = it.questTargetPlaceName,
                            )
                        }
                    clubQuest.content.targets.forEach { questTarget ->
                        questTarget.places.forEach { questTargetPlace ->
                            val placeKey = ClubQuestPlaceKey(
                                questId = clubQuest.id,
                                displayedName = questTarget.displayedName,
                                placeName = questTargetPlace.name,
                            )
                            val existingResult = existingResultByPlaceKey[placeKey]
                            if (existingResult != null) {
                                if (existingResult.isCompleted != questTargetPlace.isCompleted ||
                                    existingResult.isClosed != questTargetPlace.isClosed ||
                                    existingResult.isNotAccessible != questTargetPlace.isNotAccessible
                                ) {
                                    existingResult.isCompleted = questTargetPlace.isCompleted
                                    existingResult.isClosed = questTargetPlace.isClosed
                                    existingResult.isNotAccessible = questTargetPlace.isNotAccessible
                                    clubQuestResultRepository.add(existingResult)
                                }
                            } else {
                                clubQuestResultRepository.add(
                                    ClubQuestResult(
                                        id = EntityIdGenerator.generateRandom(),
                                        questId = clubQuest.id,
                                        questTitle = clubQuest.title,
                                        questTargetLng = questTarget.lng,
                                        questTargetLat = questTarget.lat,
                                        questTargetDisplayedName = questTarget.displayedName,
                                        questTargetPlaceName = questTargetPlace.name,
                                        isCompleted = questTargetPlace.isCompleted,
                                        isClosed = questTargetPlace.isClosed,
                                        isNotAccessible = questTargetPlace.isNotAccessible,
                                    )
                                )
                            }
                        }
                    }
                }
            } catch (t: Throwable) {
                ErrorReporter.report(t)
            }
        }
    }

    private data class ClubQuestPlaceKey(
        val questId: String,
        val displayedName: String,
        val placeName: String,
    )
}
