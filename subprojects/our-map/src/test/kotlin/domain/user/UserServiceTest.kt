package domain.user

import TestDataGenerator
import domain.DomainException
import domain.user.repository.UserRepository
import domain.util.Bcrypt
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.koin.test.inject

class UserServiceTest : UserDomainTestBase() {
    private val testDataGenerator = TestDataGenerator()

    private val userRepository by inject<UserRepository>()

    @Before
    fun setUp() = transactionManager.doInTransaction {
        userRepository.removeAll()
    }

    @Test
    fun `정상적인 경우`() = transactionManager.doInTransaction {
        val nickname = "nickname"
        val password = "password"
        val instagramId = "instagramId"
        val userId = testDataGenerator.createUser(
            nickname = nickname,
            password = password,
            instagramId = instagramId
        ).id

        val user = userRepository.findById(userId)!!
        Assert.assertEquals(nickname, user.nickname)
        Assert.assertTrue(Bcrypt.verify(password, user.encryptedPassword))
        Assert.assertEquals(instagramId, user.instagramId)
    }

    @Test(expected = DomainException::class)
    fun `중복된 닉네임은 허용하지 않는다`() = transactionManager.doInTransaction {
        repeat(2) {
            testDataGenerator.createUser(
                nickname = "nickname",
            )
        }
    }
}
