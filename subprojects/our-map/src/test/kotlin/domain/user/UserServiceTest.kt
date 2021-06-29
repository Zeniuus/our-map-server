package domain.user

import TestDataGenerator
import domain.DomainException
import domain.user.repository.UserRepository
import domain.util.Bcrypt
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class UserServiceTest : UserDomainTestBase() {
    private val testDataGenerator = TestDataGenerator()

    private val userRepository = koin.get<UserRepository>()

    @Before
    fun setUp() {
        userRepository.removeAll()
    }

    @Test
    fun `정상적인 경우`() {
        val nickname = "nickname"
        val email = "jsh56son@gmail.com"
        val password = "password"
        val instagramId = "instagramId"
        val userId = testDataGenerator.createUser(
            nickname = nickname,
            email = email,
            password = password,
            instagramId = instagramId
        ).id

        val user = userRepository.findById(userId)!!
        Assert.assertEquals(nickname, user.nickname)
        Assert.assertEquals(email, user.email)
        Assert.assertEquals(Bcrypt.encrypt(password), user.encryptedPassword)
        Assert.assertEquals(instagramId, user.instagramId)
    }

    @Test(expected = DomainException::class)
    fun `중복된 닉네임은 허용하지 않는다`() {
        repeat(2) {
            testDataGenerator.createUser(
                nickname = "nickname",
                email = "jsh56son$it@gmail.com",
            )
        }
    }

    @Test(expected = DomainException::class)
    fun `중복된 이메일은 허용하지 않는다`() {
        repeat(2) {
            testDataGenerator.createUser(
                nickname = "nickname$it",
                email = "jsh56son@gmail.com",
            )
        }
    }
}
