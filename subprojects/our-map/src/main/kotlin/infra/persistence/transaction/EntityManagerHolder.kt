package infra.persistence.transaction

import javax.persistence.EntityManager

object EntityManagerHolder {
    private val threadLocal = ThreadLocal<EntityManager>()

    fun get(): EntityManager? {
        return threadLocal.get()
    }

    fun set(em: EntityManager) {
        threadLocal.set(em)
    }

    fun remove() {
        threadLocal.remove()
    }
}
