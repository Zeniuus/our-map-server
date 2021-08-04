package auth

import io.ktor.http.HttpStatusCode
import org.junit.Assert
import org.junit.Test
import ourMap.protocol.GetHomeViewDataParams
import ourMap.protocol.Model
import route.OurMapServerRouteTestBase

class UserAuthenticatorTest : OurMapServerRouteTestBase() {
    @Test
    fun `정상적인 경우`() = runRouteTest {
        val user = transactionManager.doInTransaction {
            testDataGenerator.createUser()
        }
        val testClient = getTestClient(user)
        testClient.setAccessToken("invalidAccessToken")
        testClient.request("/getHomeViewData", GetHomeViewDataParams.getDefaultInstance()).apply {
            Assert.assertEquals(HttpStatusCode.Unauthorized, response.status())
            val result = getResult(Model.OurMapError::class)
            Assert.assertEquals("인증되지 않은 유저입니다.", result.message)
        }
    }
}
