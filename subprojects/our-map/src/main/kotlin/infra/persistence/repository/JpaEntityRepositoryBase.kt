package infra.persistence.repository

import domain.EntityRepository
import infra.persistence.transaction.EntityManagerHolder

open class JpaEntityRepositoryBase<ENTITY : Any, ID>(
    private val clazz: Class<ENTITY>,
) : EntityRepository<ENTITY, ID> {
    override fun add(entity: ENTITY): ENTITY {
        val em = EntityManagerHolder.get()!!
        em.persist(entity)
        return entity
    }

    override fun removeAll() {
        val em = EntityManagerHolder.get()!!
        em
            .createQuery("DELETE FROM ${clazz.simpleName}")
            .executeUpdate()
    }

    override fun findById(id: ID): ENTITY? {
        val em = EntityManagerHolder.get()!!
        return em.find(clazz, id)
    }
}
