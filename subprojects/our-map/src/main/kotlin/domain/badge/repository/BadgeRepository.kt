package domain.badge.repository

import domain.badge.entity.Badge

interface BadgeRepository {
    fun listAll(): List<Badge>
    fun findById(id: String): Badge?
}
