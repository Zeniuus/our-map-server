package infra.logging.jpa

import domain.logging.OurMapEvent
import domain.logging.OurMapEventLogger
import domain.util.EntityIdGenerator

class JpaOurMapEventLogger(
    private val ourMapEventEntityRepository: OurMapEventEntityRepository,
) : OurMapEventLogger {
    override fun log(event: OurMapEvent) {
        ourMapEventEntityRepository.add(
            OurMapEventEntity(
            id = EntityIdGenerator.generateRandom(),
            event = event,
        ))
    }
}
