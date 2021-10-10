package route

import quest.application.ClubQuestApplicationService
import quest.domain.entity.ClubQuest
import quest.domain.entity.ClubQuestContent
import quest.domain.service.ClubQuestService

// TODO: protobuf로 처리하기
data class ClubQuestDTO(
    val id: String,
    val title: String,
    val content: ClubQuestContent,
    val createdAtMillis: Long,
) {
    constructor(clubQuest: ClubQuest) : this(
        id = clubQuest.id,
        title = clubQuest.title,
        content = clubQuest.content,
        createdAtMillis = clubQuest.createdAt.toEpochMilli()
    )
}

// TODO: handlers layer는 불필요한 계층처럼 보인다. DTO 변환밖에 안 하고 있지 않나? 이걸 어플리케이션 계층에서 해야 하나?
class ClubQuestRouteHandlers(
    private val clubQuestApplicationService: ClubQuestApplicationService
) {
    fun createClubQuest(params: ClubQuestService.CreateParams): ClubQuestDTO {
        return ClubQuestDTO(clubQuestApplicationService.create(params))
    }

    fun deleteClubQuest(id: String) {
        clubQuestApplicationService.delete(id)
    }

    fun listClubQuests(): List<ClubQuestDTO> {
        return clubQuestApplicationService.listAll()
            .map { ClubQuestDTO(it) }
    }

    fun getClubQuest(id: String): ClubQuestDTO {
        return ClubQuestDTO(clubQuestApplicationService.getSingle(id))
    }
}
