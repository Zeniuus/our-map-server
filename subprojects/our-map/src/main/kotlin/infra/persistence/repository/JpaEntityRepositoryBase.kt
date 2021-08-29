package infra.persistence.repository

import domain.EntityRepository
import infra.persistence.transaction.EntityManagerHolder
import java.sql.SQLException

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

    override fun findById(id: ID): ENTITY {
        val em = EntityManagerHolder.get()!!
        return em.find(clazz, id) ?: throw EntityNotFoundException("${id}에 해당하는 데이터가 없습니다.")
    }

    protected fun getSingularResultOrThrow(queryResults: List<ENTITY>): ENTITY? {
        if (queryResults.size > 1) {
            throw SQLException("Query result contains more than one row.")
        }
        return queryResults.firstOrNull()
    }
}
