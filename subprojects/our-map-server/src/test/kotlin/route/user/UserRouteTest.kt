package route.user

import org.junit.Assert
import org.junit.Test
import ourMap.protocol.Common
import ourMap.protocol.LoginParams
import ourMap.protocol.SignUpParams
import route.RouteTestBase
import route.UserAuthenticator
import kotlin.random.Random

class UserRouteTest : RouteTestBase() {
    @Test
    fun testSignUp() = runRouteTest {
        val nickname = Random.nextBytes(32).toString()
        val params = SignUpParams.newBuilder()
            .setNickname(nickname)
            .setInstagramId(Common.StringValue.newBuilder().setValue("instagramId"))
            .setPassword("password")
            .build()
        requestWithoutAuth("/signUp", params).apply {
            Assert.assertNotNull(response.headers[UserAuthenticator.accessTokenHeader])
        }
    }

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
