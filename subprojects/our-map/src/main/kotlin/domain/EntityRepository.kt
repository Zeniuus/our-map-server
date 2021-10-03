package domain

interface EntityRepository<ENTITY, ID> {
    fun add(entity: ENTITY): ENTITY
    fun removeAll()
    fun findById(id: ID): ENTITY
    fun findByIdOrNull(id: ID): ENTITY?
}
