package domain.user

import domain.DomainException
import domain.DomainTestBase
import domain.user.repository.UserRepository
import domain.user.service.UserService
import domain.util.Bcrypt
import org.junit.Assert
import org.junit.Test
import org.koin.test.inject

class UserServiceTest : DomainTestBase() {
    override val koinModules = listOf(userDomainModule)

    private val userRepository by inject<UserRepository>()
    private val userService by inject<UserService>()

    @Test
    fun `정상적인 경우`() = transactionManager.doAndRollback {
        val nickname = "nickname"
        val password = "password"
        val instagramId = "instagramId"
        val user = userService.createUser(UserService.CreateUserParams(
            nickname = nickname,
            password = password,
            instagramId = instagramId,
        ))
        Assert.assertEquals(nickname, user.nickname)
        Assert.assertTrue(Bcrypt.verify(password, user.encryptedPassword))
        Assert.assertEquals(instagramId, user.instagramId)

        val newNickname = "newNickname"
        val newInstagramId = "newInstagramId"
        val updatedUser = userService.updateUserInfo(user, newNickname, newInstagramId)
        Assert.assertEquals(newNickname, updatedUser.nickname)
        Assert.assertTrue(Bcrypt.verify(password, updatedUser.encryptedPassword))
        Assert.assertEquals(newInstagramId, updatedUser.instagramId)
    }

    @Test(expected = DomainException::class)
    fun `createUser() - 중복된 닉네임은 허용하지 않는다`() = transactionManager.doAndRollback {
        repeat(2) {
            userService.createUser(UserService.CreateUserParams(
                nickname = "nickname",
                password = "password",
                instagramId = null,
            ))
        }
    }

    @Test(expected = DomainException::class)
    fun `updateUserInfo() - 중복된 닉네임은 허용하지 않는다`() = transactionManager.doAndRollback {
        val user = testDataGenerator.createUser(
            nickname = "nickname",
        )
        testDataGenerator.createUser(
            nickname = "newNickname"
        )
        userService.updateUserInfo(user, nickname = "newNickname", instagramId = null)
    }
}
