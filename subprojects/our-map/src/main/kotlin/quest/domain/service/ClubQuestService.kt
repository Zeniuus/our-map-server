package quest.domain.service

import domain.util.EntityIdGenerator
import quest.domain.entity.ClubQuest
import quest.domain.entity.ClubQuestContent
import quest.domain.repository.ClubQuestRepository
import java.time.Clock

class ClubQuestService(
    private val clock: Clock,
    private val clubQuestRepository: ClubQuestRepository,
) {
    data class CreateParams(
        val title: String,
        val rows: List<InputRow>,
    ) {
        data class InputRow(
            val lng: Double,
            val lat: Double,
            val displayedName: String,
            val placeName: String,
        )
    }

    fun create(params: CreateParams): ClubQuest {
        val targets = params.rows
            .groupBy { Triple(it.lng, it.lat, it.displayedName) }
            .map { (key, rows) ->
                val (lng, lat, displayedName) = key
                ClubQuestContent.Target(
                    lng = lng,
                    lat = lat,
                    displayedName = displayedName,
                    places = rows.map {
                        ClubQuestContent.Target.Place(name = it.placeName)
                    }
                )
            }
        return clubQuestRepository.add(ClubQuest(
            id = EntityIdGenerator.generateRandom(),
            title = params.title.trim(),
            content = ClubQuestContent(targets),
            createdAt = clock.instant(),
        ))
    }

    fun delete(clubQuest: ClubQuest): ClubQuest {
        clubQuest.deletedAt = clock.instant()
        return clubQuestRepository.add(clubQuest)
    }

    data class ClubQuestTargetPlaceInfo(
        val lng: Double,
        val lat: Double,
        val targetDisplayedName: String,
        val placeName: String,
    ) {
        fun findPlace(clubQuest: ClubQuest): ClubQuestContent.Target.Place? {
            return clubQuest.content.targets
                .find { it.lng == lng && it.lat == lat && it.displayedName == targetDisplayedName }
                ?.places?.find { it.name == placeName }
        }
    }

    fun setPlaceIsCompleted(clubQuest: ClubQuest, targetPlaceInfo: ClubQuestTargetPlaceInfo, isCompleted: Boolean): ClubQuest {
        val place = targetPlaceInfo.findPlace(clubQuest) ?: return clubQuest
        place.isCompleted = isCompleted
        return clubQuestRepository.add(clubQuest)
    }

    fun setPlaceIsClosed(clubQuest: ClubQuest, targetPlaceInfo: ClubQuestTargetPlaceInfo, isClosed: Boolean): ClubQuest {
        val place = targetPlaceInfo.findPlace(clubQuest) ?: return clubQuest
        place.isClosed = isClosed
        return clubQuestRepository.add(clubQuest)
    }

    fun setPlaceIsNotAccessible(clubQuest: ClubQuest, targetPlaceInfo: ClubQuestTargetPlaceInfo, isNotAccessible: Boolean): ClubQuest {
        val place = targetPlaceInfo.findPlace(clubQuest) ?: return clubQuest
        place.isNotAccessible = isNotAccessible
        return clubQuestRepository.add(clubQuest)
    }
}
