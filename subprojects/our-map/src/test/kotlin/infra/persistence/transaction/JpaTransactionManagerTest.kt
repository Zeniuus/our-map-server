package infra.persistence.transaction

import application.TransactionIsolationLevel
import infra.persistence.configuration.HibernateJpaConfiguration
import org.hibernate.internal.SessionImpl
import org.junit.Assert
import org.junit.Test
import java.sql.Connection

class JpaTransactionManagerTest {
    private val transactionManager = JpaTransactionManager(HibernateJpaConfiguration.createEntityManagerFactory())

    @Test
    fun nestedTransactionTest() {
        transactionManager.doInTransaction {
            transactionManager.doInTransaction(TransactionIsolationLevel.SERIALIZABLE) {
                Assert.assertTrue(transactionManager.isInTransaction())

                // inner transaction에서 지정한 isolation level은 무시되고, outer transaction에 그대로 참여하게 된다.
                val connection = getCurrentConnection()
                Assert.assertEquals(TransactionIsolationLevel.REPEATABLE_READ.toConnectionIsolationLevel(), connection.transactionIsolation)
            }
            // inner transaction이 끝나도 outer transaction은 여전히 살아 있다.
            Assert.assertTrue(transactionManager.isInTransaction())

            val connection = getCurrentConnection()
            Assert.assertEquals(TransactionIsolationLevel.REPEATABLE_READ.toConnectionIsolationLevel(), connection.transactionIsolation)
        }

        transactionManager.doInTransaction(TransactionIsolationLevel.SERIALIZABLE) {
            transactionManager.doInTransaction {
                Assert.assertTrue(transactionManager.isInTransaction())

                // inner transaction에서 지정한 isolation level은 무시되고, outer transaction에 그대로 참여하게 된다.
                val connection = getCurrentConnection()
                Assert.assertEquals(TransactionIsolationLevel.SERIALIZABLE.toConnectionIsolationLevel(), connection.transactionIsolation)
            }
            // inner transaction이 끝나도 outer transaction은 여전히 살아 있다.
            Assert.assertTrue(transactionManager.isInTransaction())

            val connection = getCurrentConnection()
            Assert.assertEquals(TransactionIsolationLevel.SERIALIZABLE.toConnectionIsolationLevel(), connection.transactionIsolation)
        }
    }

    private fun getCurrentConnection(): Connection {
        return EntityManagerHolder.get()!!.unwrap(SessionImpl::class.java).connection()
    }
}
