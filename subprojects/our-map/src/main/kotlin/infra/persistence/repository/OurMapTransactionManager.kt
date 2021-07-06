package infra.persistence.repository

import org.koin.core.context.GlobalContext
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory

object OurMapTransactionManager {
    private val entityManagerFactory = GlobalContext.getKoinApplicationOrNull()!!.koin.get<EntityManagerFactory>()
    private val threadLocal = ThreadLocal<EntityManager>()

    // 멱등적이다.
    fun start() {
        val curr = threadLocal.get()
        if (curr == null) {
            val new = entityManagerFactory.createEntityManager()
            new.transaction.begin()
            threadLocal.set(new)
        }
    }

    fun isInTransaction(): Boolean {
        return threadLocal.get() != null
    }

    fun getEntityManager(): EntityManager {
        return threadLocal.get() ?: throw IllegalStateException("EntityManager does not exist. getEntityManager() should be called between start() and commit().")
    }

    // 멱등적이다.
    fun commit() {
        val curr = threadLocal.get()
        if (curr != null) {
            curr.transaction.commit()
            threadLocal.remove()
        }
    }

    fun rollback() {
        val curr = threadLocal.get()
        if (curr != null) {
            curr.transaction.rollback()
            threadLocal.remove()
        }
    }
}

fun <T> transactional(block: () -> T): T {
    try {
        OurMapTransactionManager.start()
        val result = block()
        OurMapTransactionManager.commit()
        return result
    } catch (t: Throwable) {
        if (OurMapTransactionManager.isInTransaction()) {
            OurMapTransactionManager.rollback()
        }
        throw t
    }
}
