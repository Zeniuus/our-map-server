package route

import org.junit.Assert
import org.junit.Test
import ourMap.protocol.Common
import ourMap.protocol.UpdateUserInfoParams
import ourMap.protocol.UpdateUserInfoResult

class UpdateUserInfoTest : OurMapServerRouteTestBase() {
    @Test
    fun updateUserInfoTest() = runRouteTest {
        val user = transactionManager.doInTransaction {
            testDataGenerator.createUser()
        }

        val changedNickname = "newNickname"
        val changedInstagramId = "newInstagramId"
        Assert.assertNotEquals(user.nickname, changedNickname)
        Assert.assertNotEquals(user.instagramId, changedInstagramId)

        val params = UpdateUserInfoParams.newBuilder()
            .setNickname(changedNickname)
            .setInstagramId(Common.StringValue.newBuilder().setValue(changedInstagramId).build())
            .build()
        getTestClient(user).request("/updateUserInfo", params).apply {
            val result = getResult(UpdateUserInfoResult::class)
            Assert.assertEquals(user.id, result.user.id)
            Assert.assertEquals(changedNickname, result.user.nickname)
            Assert.assertEquals(changedInstagramId, result.user.instagramId.value)
        }
    }
}
