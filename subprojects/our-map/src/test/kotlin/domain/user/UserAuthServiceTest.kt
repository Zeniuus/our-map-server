package domain.user

import TestDataGenerator
import domain.user.exception.UserAuthenticationException
import domain.user.repository.UserRepository
import domain.user.service.UserAuthService
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class UserAuthServiceTest : UserDomainTestBase() {
    private val testDataGenerator = TestDataGenerator()

    private val userRepository = koin.get<UserRepository>()
    private val userAuthService = koin.get<UserAuthService>()

    @Before
    fun setUp() {
        userRepository.removeAll()
    }

    @Test
    fun `정상적인 경우`() {
        val email = "jsh56son@gmail.com"
        val password = "password"
        val createdUser = testDataGenerator.createUser(
            email = email,
            password = password,
        )

        val user = userAuthService.authenticate(email, password)
        Assert.assertEquals(createdUser.id, user.id)
    }

    @Test
    fun `이메일이 잘못되면 로그인에 실패한다`() {
        val email = "jsh56son@gmail.com"
        val password = "password"
        testDataGenerator.createUser(
            email = email,
            password = password,
        )

        try {
            userAuthService.authenticate("wrongEmail@gmail.com", password)
            Assert.fail()
        } catch (e: UserAuthenticationException) {
            Assert.assertEquals(UserAuthenticationException.ErrorCode.USER_DOES_NOT_EXIST, e.errorCode)
        }
    }

    @Test
    fun `비밀번호가 잘못되면 로그인에 실패한다`() {
        val email = "jsh56son@gmail.com"
        val password = "password"
        testDataGenerator.createUser(
            email = email,
            password = password,
        )

        try {
            userAuthService.authenticate(email, "wrongPassword")
            Assert.fail()
        } catch (e: UserAuthenticationException) {
            Assert.assertEquals(UserAuthenticationException.ErrorCode.WRONG_PASSWORD, e.errorCode)
        }
    }
}