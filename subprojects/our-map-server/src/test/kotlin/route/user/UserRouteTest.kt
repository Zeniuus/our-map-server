package route.user

import org.junit.Assert
import org.junit.Test
import ourMap.protocol.Common
import ourMap.protocol.SignUpParams
import route.RouteTestBase
import route.UserAuthenticator

class UserRouteTest : RouteTestBase() {
    @Test
    fun testSignUp() = runRouteTest {
        val params = SignUpParams.newBuilder()
            .setNickname("swann")
            .setInstagramId(Common.StringValue.newBuilder().setValue("instagramId"))
            .setPassword("password")
            .build()
        request("/signUp", params).apply {
            Assert.assertNotNull(response.headers[UserAuthenticator.accessTokenHeader])
        }
    }
}
