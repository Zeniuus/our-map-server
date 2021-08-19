package domain.badge

import domain.DomainTestBase
import domain.badge.service.UserBadgeIssueService
import domain.user.userDomainModule
import org.junit.Assert
import org.junit.Test
import org.koin.test.inject

class UserBadgeIssueServiceTest : DomainTestBase() {
    override val koinModules = listOf(userDomainModule, badgeDomainModule)

    private val userBadgeIssueService by inject<UserBadgeIssueService>()

    @Test
    fun `발급할 뱃지 목록 조회 및 발급 테스트`() = transactionManager.doAndRollback {
        val user = testDataGenerator.createUser()
        val place = testDataGenerator.createBuildingAndPlace()
        testDataGenerator.registerBuildingAndPlaceAccessibility(place = place, user = user)

        val badgesToIssue1 = userBadgeIssueService.findBadgesToIssue(user)
        Assert.assertEquals(1, badgesToIssue1.size)
        Assert.assertEquals("첫 정복 뱃지", badgesToIssue1[0].name)

        userBadgeIssueService.issueBadge(user, badgesToIssue1[0])

        val badgesToIssue2 = userBadgeIssueService.findBadgesToIssue(user)
        Assert.assertEquals(0, badgesToIssue2.size)

        repeat(2) {
            val otherPlace = testDataGenerator.createBuildingAndPlace()
            testDataGenerator.registerBuildingAndPlaceAccessibility(place = otherPlace, user = user)
        }

        val badgesToIssue3 = userBadgeIssueService.findBadgesToIssue(user)
        Assert.assertEquals(1, badgesToIssue3.size)
        Assert.assertEquals("숙련자", badgesToIssue3[0].name)
    }
}
