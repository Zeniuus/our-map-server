package infra.logging.jpa

import infra.persistence.repository.JpaEntityRepositoryBase

class JpaOurMapEventEntityRepository :
    OurMapEventEntityRepository,
    JpaEntityRepositoryBase<OurMapEventEntity, String>(OurMapEventEntity::class.java)
