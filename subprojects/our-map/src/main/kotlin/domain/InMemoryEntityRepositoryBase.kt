package domain

import kotlin.reflect.KProperty

abstract class InMemoryEntityRepositoryBase<ENTITY : Any, ID : Any> : EntityRepository<ENTITY, ID> {
    protected val entities = mutableListOf<ENTITY>()

    override fun add(entity: ENTITY): ENTITY {
        entities.add(entity)
        return entity
    }

    override fun removeAll() {
        entities.clear()
    }

    override fun findById(id: ID): ENTITY? {
        return entities.find {
            val entityId = (it::class.members.find { it.name == "id" } as KProperty<String>?)?.getter?.call(it)
            entityId == id
        }
    }
}
