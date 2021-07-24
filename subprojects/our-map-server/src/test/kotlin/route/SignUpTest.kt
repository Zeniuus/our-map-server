package route

import auth.UserAuthenticator
import org.junit.Assert
import org.junit.Test
import ourMap.protocol.Common
import ourMap.protocol.SignUpParams
import kotlin.random.Random

class SignUpTest : OurMapServerRouteTestBase() {
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
}
