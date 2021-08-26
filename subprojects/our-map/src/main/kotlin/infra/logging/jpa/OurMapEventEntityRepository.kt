package infra.logging.jpa

import domain.EntityRepository

interface OurMapEventEntityRepository : EntityRepository<OurMapEventEntity, String>
