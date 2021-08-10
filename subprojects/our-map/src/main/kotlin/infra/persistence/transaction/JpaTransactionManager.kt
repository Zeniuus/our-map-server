package infra.persistence.transaction

import application.TransactionIsolationLevel
import application.TransactionManager
import org.hibernate.internal.SessionImpl
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory

class JpaTransactionManager(
    private val entityManagerFactory: EntityManagerFactory,
) : TransactionManager {
    // 멱등적이다.
    fun start(isolationLevel: TransactionIsolationLevel = TransactionIsolationLevel.REPEATABLE_READ) {
        val curr = EntityManagerHolder.get()
        if (curr == null) {
            val new = entityManagerFactory.createEntityManager()
            if (isolationLevel != TransactionIsolationLevel.REPEATABLE_READ) {
                val session = new.unwrap(SessionImpl::class.java)
                session.connection().transactionIsolation = isolationLevel.toConnectionIsolationLevel()
            }
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
            curr.close()
            curr.transaction.commit()
            EntityManagerHolder.remove()
        }
    }

    fun rollback() {
        val curr = EntityManagerHolder.get()
        if (curr != null) {
            curr.close()
            curr.transaction.rollback()
            EntityManagerHolder.remove()
        }
    }

    override fun <T> doInTransaction(block: () -> T): T {
        return doInTransaction(TransactionIsolationLevel.REPEATABLE_READ, block)
    }

    override fun <T> doInTransaction(isolationLevel: TransactionIsolationLevel, block: () -> T): T {
        val isNestedTransaction = isInTransaction()
        try {
            // nested transaction일 경우 이미 열려 있는 트랜잭션에 그대로 참여한다.
            if (!isNestedTransaction) {
                start(isolationLevel)
            }
            val result = block()

            if (!isNestedTransaction) {
                commit()
            }
            return result
        } catch (t: Throwable) {
            if (!isNestedTransaction) {
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
