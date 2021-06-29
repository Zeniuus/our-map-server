import domain.user.entity.User
import domain.user.service.UserService
import domain.user.userDomainModule
import org.koin.dsl.koinApplication

class TestDataGenerator {
    private val koin = koinApplication {
        modules(userDomainModule)
    }.koin

    private val userService = koin.get<UserService>()

    fun createUser(
        nickname: String = "nickname",
        email: String = "jsh56son@gmail.com",
        password: String = "password",
        instagramId: String? = null
    ): User {
        return userService.createUser(
            UserService.CreateUserParams(
                nickname,
                email,
                password,
                instagramId,
            )
        )
    }
}