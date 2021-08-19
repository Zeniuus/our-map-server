package domain.badge.repository

import domain.EntityRepository
import domain.badge.entity.UserBadgeIssue
import domain.user.entity.User

interface UserBadgeIssueRepository : EntityRepository<UserBadgeIssue, String> {
    fun findByUser(user: User): List<UserBadgeIssue>
}
