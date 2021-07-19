package infra.persistence.transaction

import application.TransactionManager
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory

class JpaTransactionManager(
    private val entityManagerFactory: EntityManagerFactory,
) : TransactionManager {
    // 멱등적이다.
    fun start() {
        val curr = EntityManagerHolder.get()
        if (curr == null) {
            val new = entityManagerFactory.createEntityManager()
            new.transaction.begin()
            EntityManagerHolder.set(new)
        }
    }

    fun isInTransaction(): Boolean {
        return EntityManagerHolder.get() != null
    }

    fun getEntityManager(): EntityManager {
        return EntityManagerHolder.get() ?: throw IllegalStateException("EntityManager does not exist. getEntityManager() should be called between start() and commit().")
    }

    // 멱등적이다.
    fun commit() {
        val curr = EntityManagerHolder.get()
        if (curr != null) {
            curr.transaction.commit()
            EntityManagerHolder.remove()
        }
    }

    fun rollback() {
        val curr = EntityManagerHolder.get()
        if (curr != null) {
            curr.transaction.rollback()
            EntityManagerHolder.remove()
        }
    }

    override fun <T> doInTransaction(block: () -> T): T {
        try {
            start()
            val result = block()
            commit()
            return result
        } catch (t: Throwable) {
            if (isInTransaction()) {
                rollback()
            }
            throw t
        }
    }

    override fun doAndRollback(block: () -> Any) {
        try {
            start()
            block()
            rollback()
        } catch (t: Throwable) {
            if (isInTransaction()) {
                rollback()
            }
            throw t
        }
    }
}
