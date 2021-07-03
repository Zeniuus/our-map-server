package infra.persistence.repository

import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory

class GlobalEntityManagerHolder private constructor (
    private val entityManagerFactory: EntityManagerFactory
) {
    companion object {
        private var instance: GlobalEntityManagerHolder? = null

        @Synchronized
        fun getInstance(entityManagerFactory: EntityManagerFactory): GlobalEntityManagerHolder {
            if (instance != null) {
                return instance!!
            }
            instance = GlobalEntityManagerHolder(entityManagerFactory)
            return instance!!
        }
    }

    private val threadLocal = ThreadLocal<EntityManager>()

    // TODO: 트랜잭션 관리
    // TODO: GlobalEntityManagerHolder를 bean으로 안 만드는 방법은 없나?
    //       얘를 bean으로 만들면 트랜잭션 관리하는 코드도 얘를 주입받아야 해서
    //       bean으로 만들어야 하는데, 좀 구린 것 같다.
    fun get(): EntityManager {
        val curr = threadLocal.get()
        if (curr != null) {
            return curr
        }

        val new = entityManagerFactory.createEntityManager()
        new.transaction.begin()
        threadLocal.set(new)
        return new
    }

    fun clear() {
        val curr = threadLocal.get()
        curr.transaction.commit()
        threadLocal.remove()
    }
}
