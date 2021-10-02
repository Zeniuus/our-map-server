package application.accessibility

import application.TransactionIsolationLevel
import application.TransactionManager
import domain.accessibility.entity.BuildingAccessibility
import domain.accessibility.entity.BuildingAccessibilityComment
import domain.accessibility.entity.PlaceAccessibility
import domain.accessibility.entity.PlaceAccessibilityComment
import domain.accessibility.repository.BuildingAccessibilityCommentRepository
import domain.accessibility.repository.BuildingAccessibilityRepository
import domain.accessibility.repository.PlaceAccessibilityCommentRepository
import domain.accessibility.repository.PlaceAccessibilityRepository
import domain.accessibility.service.BuildingAccessibilityCommentService
import domain.accessibility.service.BuildingAccessibilityService
import domain.accessibility.service.PlaceAccessibilityCommentService
import domain.accessibility.service.PlaceAccessibilityService
import domain.place.repository.PlaceRepository

class AccessibilityApplicationService(
    private val transactionManager: TransactionManager,
    private val placeRepository: PlaceRepository,
    private val placeAccessibilityRepository: PlaceAccessibilityRepository,
    private val placeAccessibilityCommentRepository: PlaceAccessibilityCommentRepository,
    private val buildingAccessibilityRepository: BuildingAccessibilityRepository,
    private val buildingAccessibilityCommentRepository: BuildingAccessibilityCommentRepository,
    private val placeAccessibilityService: PlaceAccessibilityService,
    private val placeAccessibilityCommentService: PlaceAccessibilityCommentService,
    private val buildingAccessibilityService: BuildingAccessibilityService,
    private val buildingAccessibilityCommentService: BuildingAccessibilityCommentService,
    private val accessibilityRegisteredEventHandler: AccessibilityRegisteredEventHandler,
) {
    data class GetAccessibilityResult(
        val buildingAccessibility: BuildingAccessibility?,
        val buildingAccessibilityComments: List<BuildingAccessibilityComment>,
        val placeAccessibility: PlaceAccessibility?,
        val placeAccessibilityComments: List<PlaceAccessibilityComment>,
    )

    fun getAccessibility(placeId: String): GetAccessibilityResult = transactionManager.doInTransaction {
        val place = placeRepository.findById(placeId)
        GetAccessibilityResult(
            buildingAccessibility = buildingAccessibilityRepository.findByBuildingId(place.building.id),
            buildingAccessibilityComments = buildingAccessibilityCommentRepository.findByBuildingId(place.building.id),
            placeAccessibility = placeAccessibilityRepository.findByPlaceId(placeId),
            placeAccessibilityComments = placeAccessibilityCommentRepository.findByPlaceId(placeId)
        )
    }

    data class RegisterAccessibilityResult(
        val placeAccessibility: PlaceAccessibility,
        val placeAccessibilityComment: PlaceAccessibilityComment?,
        val buildingAccessibility: BuildingAccessibility?,
        val buildingAccessibilityComment: BuildingAccessibilityComment?,
    )

    fun register(
        createPlaceAccessibilityParams: PlaceAccessibilityService.CreateParams,
        createPlaceAccessibilityCommentParams: PlaceAccessibilityCommentService.CreateParams?,
        createBuildingAccessibilityParams: BuildingAccessibilityService.CreateParams?,
        createBuildingAccessibilityCommentParams: BuildingAccessibilityCommentService.CreateParams?,
    ): RegisterAccessibilityResult = transactionManager.doInTransaction(TransactionIsolationLevel.SERIALIZABLE) {
        val placeAccessibility = placeAccessibilityService.create(createPlaceAccessibilityParams)
        val placeAccessibilityComment = createPlaceAccessibilityCommentParams?.let { placeAccessibilityCommentService.create(it) }
        val buildingAccessibility = createBuildingAccessibilityParams?.let { buildingAccessibilityService.create(it) }
        val buildingAccessibilityComment = createBuildingAccessibilityCommentParams?.let { buildingAccessibilityCommentService.create(it) }

        accessibilityRegisteredEventHandler.handle(placeAccessibility, buildingAccessibility)

        RegisterAccessibilityResult(
            placeAccessibility = placeAccessibility,
            placeAccessibilityComment = placeAccessibilityComment,
            buildingAccessibility = buildingAccessibility,
            buildingAccessibilityComment = buildingAccessibilityComment,
        )
    }

    fun registerBuildingAccessibilityComment(
        params: BuildingAccessibilityCommentService.CreateParams,
    ): BuildingAccessibilityComment = transactionManager.doInTransaction(TransactionIsolationLevel.SERIALIZABLE) {
        buildingAccessibilityCommentService.create(params)
    }

    fun registerPlaceAccessibilityComment(
        params: PlaceAccessibilityCommentService.CreateParams,
    ): PlaceAccessibilityComment = transactionManager.doInTransaction(TransactionIsolationLevel.SERIALIZABLE) {
        placeAccessibilityCommentService.create(params)
    }
}
