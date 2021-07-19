package domain.user

import TestDataGenerator
import domain.user.exception.UserAuthenticationException
import domain.user.service.UserAuthService
import org.junit.Assert
import org.junit.Test
import org.koin.test.inject

class UserAuthServiceTest : UserDomainTestBase() {
    private val testDataGenerator = TestDataGenerator()

    private val userAuthService by inject<UserAuthService>()

    @Test
    fun `정상적인 경우`() = transactionManager.doAndRollback {
        val nickname = "swann"
        val password = "password"
        val createdUser = testDataGenerator.createUser(
            nickname = nickname,
            password = password,
        )

        val user = userAuthService.authenticate(nickname, password)
        Assert.assertEquals(createdUser.id, user.id)
    }

    @Test
    fun `닉네임이 잘못되면 로그인에 실패한다`() = transactionManager.doAndRollback {
        val nickname = "swann"
        val password = "password"
        testDataGenerator.createUser(
            nickname = nickname,
            password = password,
        )

        try {
            userAuthService.authenticate("wrongNickname", password)
            Assert.fail()
        } catch (e: UserAuthenticationException) {
            Assert.assertEquals(UserAuthenticationException.ErrorCode.USER_DOES_NOT_EXIST, e.errorCode)
        }
    }

    @Test
    fun `비밀번호가 잘못되면 로그인에 실패한다`() = transactionManager.doAndRollback {
        val nickname = "swann"
        val password = "password"
        testDataGenerator.createUser(
            nickname = nickname,
            password = password,
        )

        try {
            userAuthService.authenticate(nickname, "wrongPassword")
            Assert.fail()
        } catch (e: UserAuthenticationException) {
            Assert.assertEquals(UserAuthenticationException.ErrorCode.WRONG_PASSWORD, e.errorCode)
        }
    }
}
