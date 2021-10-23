package route

import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
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

class ClubQuestRouteHandlers(
    private val clubQuestApplicationService: ClubQuestApplicationService
) {
    fun createClubQuest(route: Route) {
        route.post("/api/clubQuests/create") {
            val params = call.receive<ClubQuestService.CreateParams>()
            call.respond(ClubQuestDTO(clubQuestApplicationService.create(params)))
        }
    }

    fun deleteClubQuest(route: Route) {
        route.get("/api/clubQuests/{id}/delete") {
            val id = call.parameters["id"]!!
            call.respond(clubQuestApplicationService.delete(id))
        }
    }

    fun getClubQuest(route: Route) {
        route.get("/api/clubQuests/{id}") {
            val id = call.parameters["id"]!!
            call.respond(ClubQuestDTO(clubQuestApplicationService.getSingle(id)))
        }
    }

    fun listClubQuests(route: Route) {
        route.get("/api/clubQuests") {
            call.respond(
                clubQuestApplicationService.listAll()
                    .map { ClubQuestDTO(it) }
            )
        }
    }

    fun setPlaceIsCompleted(route: Route) {
        route.post("/api/clubQuests/{id}/setPlaceIsCompleted/{isCompleted}") {
            val id = call.parameters["id"]!!
            val isCompleted = call.parameters["isCompleted"]!!.toBoolean()
            val targetPlaceInfo = call.receive<ClubQuestService.ClubQuestTargetPlaceInfo>()
            call.respond(ClubQuestDTO(clubQuestApplicationService.setPlaceIsCompleted(id, targetPlaceInfo, isCompleted)))
        }
    }

    fun setPlaceIsClosed(route: Route) {
        route.post("/api/clubQuests/{id}/setPlaceIsClosed/{isClosed}") {
            val id = call.parameters["id"]!!
            val isClosed = call.parameters["isClosed"]!!.toBoolean()
            val targetPlaceInfo = call.receive<ClubQuestService.ClubQuestTargetPlaceInfo>()
            call.respond(ClubQuestDTO(clubQuestApplicationService.setPlaceIsClosed(id, targetPlaceInfo, isClosed)))
        }
    }
}
