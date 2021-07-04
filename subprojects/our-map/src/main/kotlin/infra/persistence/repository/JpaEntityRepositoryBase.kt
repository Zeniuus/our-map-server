package infra.persistence.repository

import domain.EntityRepository

open class JpaEntityRepositoryBase<ENTITY : Any, ID>(
    private val clazz: Class<ENTITY>,
    private val entityManagerHolder: GlobalEntityManagerHolder,
) : EntityRepository<ENTITY, ID> {
    override fun add(entity: ENTITY): ENTITY {
        val em = entityManagerHolder.get()
        em.persist(entity)
        return entity
    }

    override fun removeAll() {
        val em = entityManagerHolder.get()
        em
            .createQuery("DELETE FROM ${clazz.simpleName}")
            .executeUpdate()
    }

    override fun findById(id: ID): ENTITY? {
        val em = entityManagerHolder.get()
        return em.find(clazz, id)
    }
}
