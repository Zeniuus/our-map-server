package job

import domain.util.EntityIdGenerator
import org.junit.Assert
import org.junit.Test
import org.koin.test.inject
import quest.domain.entity.ClubQuest
import quest.domain.entity.ClubQuestContent
import quest.domain.repository.ClubQuestRepository
import quest.domain.repository.ClubQuestResultRepository
import java.time.Clock

class EtlClubQuestResultJobTest : OurMapServerAdminJobTestBase() {
    override val koinModules = listOf(
        ourMapServerAdminJobModule,
    )

    private val clock by inject<Clock>()
    private val clubQuestRepository by inject<ClubQuestRepository>()
    private val clubQuestResultRepository by inject<ClubQuestResultRepository>()
    private val etlClubQuestResultJob by inject<EtlClubQuestResultJob>()

    @Test
    fun test() {
        val quest = transactionManager.doInTransaction {
            clubQuestRepository.add(ClubQuest(
                id = EntityIdGenerator.generateRandom(),
                title = "퀘스트 1",
                content = ClubQuestContent(
                    targets = listOf(ClubQuestContent.Target(
                        lng = 127.0,
                        lat = 37.0,
                        displayedName = "건물 1",
                        places = listOf(
                            ClubQuestContent.Target.Place(name = "장소 1"),
                            ClubQuestContent.Target.Place(name = "장소 2"),
                        ),
                    )),
                ),
                createdAt = clock.instant(),
            ))
        }

        repeat(2) { // 멱등성 테스트를 위해 2번 실행한다.
            etlClubQuestResultJob.run()
            assertJobRunCorrectly(quest)
        }

        val updatedQuest = transactionManager.doInTransaction {
            val reloadedQuest = clubQuestRepository.findById(quest.id)
            reloadedQuest.content.targets[0].places[1].isCompleted = true
            clubQuestRepository.add(reloadedQuest)
        }

        repeat(2) { // 멱등성 테스트를 위해 2번 실행한다.
            etlClubQuestResultJob.run()
            assertJobRunCorrectly(updatedQuest)
        }
    }

    private fun assertJobRunCorrectly(quest: ClubQuest) {
        transactionManager.doInTransaction {
            val results = clubQuestResultRepository.listAll()
            Assert.assertEquals(2, results.size)
            results.forEach { result ->
                Assert.assertEquals(quest.id, result.questId)
                Assert.assertEquals(quest.title, result.questTitle)
                Assert.assertEquals(quest.content.targets[0].lng, result.questTargetLng, 0.0000001)
                Assert.assertEquals(quest.content.targets[0].lat, result.questTargetLat, 0.0000001)
                Assert.assertEquals(quest.content.targets[0].displayedName, result.questTargetDisplayedName)

                val questTargetPlace = quest.content.targets[0].places.find { it.name == result.questTargetPlaceName }!!
                Assert.assertEquals(questTargetPlace.isCompleted, result.isCompleted)
                Assert.assertEquals(questTargetPlace.isClosed, result.isClosed)
                Assert.assertEquals(questTargetPlace.isNotAccessible, result.isNotAccessible)
            }
        }
    }
}
