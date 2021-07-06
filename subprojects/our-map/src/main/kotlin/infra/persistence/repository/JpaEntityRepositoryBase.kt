package infra.persistence.repository

import domain.EntityRepository

open class JpaEntityRepositoryBase<ENTITY : Any, ID>(
    private val clazz: Class<ENTITY>,
) : EntityRepository<ENTITY, ID> {
    override fun add(entity: ENTITY): ENTITY {
        val em = OurMapTransactionManager.getEntityManager()
        em.persist(entity)
        return entity
    }

    override fun removeAll() {
        val em = OurMapTransactionManager.getEntityManager()
        em
            .createQuery("DELETE FROM ${clazz.simpleName}")
            .executeUpdate()
    }

    override fun findById(id: ID): ENTITY? {
        val em = OurMapTransactionManager.getEntityManager()
        return em.find(clazz, id)
    }
}
