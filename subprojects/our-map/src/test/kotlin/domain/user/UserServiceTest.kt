package domain.user

import domain.DomainException
import domain.user.repository.UserRepository
import domain.user.service.UserService
import domain.util.Bcrypt
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.koin.dsl.koinApplication

class UserServiceTest {
    private val koinApplication = koinApplication {
        modules(userDomainModule)
    }

    private val userService = koinApplication.koin.get<UserService>()
    private val userRepository = koinApplication.koin.get<UserRepository>()

    @Before
    fun setUp() {
        userRepository.removeAll()
    }

    @Test
    fun `정상적인 경우`() {
        val nickname = "nickname"
        val email = "jsh56son@gmail.com"
        val password = "password"
        val userId = userService.createUser(
            nickname = nickname,
            email = email,
            password = password,
            instagramId = null,
        ).id

        val user = userRepository.findById(userId)!!
        Assert.assertEquals(nickname, user.nickname)
        Assert.assertEquals(email, user.email)
        Assert.assertEquals(Bcrypt.encrypt(password), user.encryptedPassword)
    }

    @Test(expected = DomainException::class)
    fun `중복된 닉네임은 허용하지 않는다`() {
        repeat(2) {
            userService.createUser(
                nickname = "nickname",
                email = "jsh56son$it@gmail.com",
                password = "password$it",
                instagramId = null,
            )
        }
    }

    @Test(expected = DomainException::class)
    fun `중복된 이메일은 허용하지 않는다`() {
        repeat(2) {
            userService.createUser(
                nickname = "nickname$it",
                email = "jsh56son@gmail.com",
                password = "password$it",
                instagramId = null,
            )
        }
    }
}
