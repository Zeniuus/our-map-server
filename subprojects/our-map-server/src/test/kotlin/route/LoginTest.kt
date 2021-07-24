package route

import auth.UserAuthenticator
import org.junit.Assert
import org.junit.Test
import ourMap.protocol.LoginParams
import kotlin.random.Random

class LoginTest : RouteTestBase() {
    @Test
    fun testLogin() = runRouteTest {
        val nickname = Random.nextBytes(32).toString()
        val password = "password"
        transactionManager.doInTransaction {
            testDataGenerator.createUser(nickname = nickname, password = password)
        }
        val params = LoginParams.newBuilder()
            .setNickname(nickname)
            .setPassword(password)
            .build()
        requestWithoutAuth("/login", params).apply {
            Assert.assertNotNull(response.headers[UserAuthenticator.accessTokenHeader])
        }
    }
}
