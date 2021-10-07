package route

import org.junit.Assert
import org.junit.Test
import ourMap.protocol.ListAdministrativeAreasParams
import ourMap.protocol.ListAdministrativeAreasResult

class ListAdministrativeAreasTest : OurMapServerRouteTestBase() {
    @Test
    fun testListAdministrativeAreas() = runRouteTest {
        val user = transactionManager.doInTransaction {
            testDataGenerator.createUser()
        }
        getTestClient(user).request("/listAdministrativeAreas", ListAdministrativeAreasParams.getDefaultInstance()).apply {
            val result = getResult(ListAdministrativeAreasResult::class)
            val siGunGus = result.siGunGusList
            val eupMyeonDongs = result.eupMyeonDongsList

            Assert.assertEquals(3, siGunGus.size)
            Assert.assertEquals(31, eupMyeonDongs.size)
            val siGunGuIds = siGunGus.map { it.id }.toSet()
            Assert.assertTrue(eupMyeonDongs.all { it.siGunGuId in siGunGuIds })
        }
    }
}
