package domain.util

import java.util.UUID

object EntityIdRandomGenerator {
    fun generate(): String {
        return UUID.randomUUID().toString()
    }
}