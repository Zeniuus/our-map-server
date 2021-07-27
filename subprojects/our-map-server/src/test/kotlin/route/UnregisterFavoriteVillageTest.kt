package route

import domain.village.repository.VillageRepository
import domain.village.service.VillageService
import org.junit.Assert
import org.junit.Test
import org.koin.test.inject
import ourMap.protocol.RegisterFavoriteVillageParams
import ourMap.protocol.UnregisterFavoriteVillageParams
import ourMap.protocol.UnregisterFavoriteVillageResult

class UnregisterFavoriteVillageTest : OurMapServerRouteTestBase() {
    private val villageRepository by inject<VillageRepository>()
    private val villageService by inject<VillageService>()

    @Test
    fun unregisterFavoriteVillageTest() = runRouteTest {
        val user = transactionManager.doInTransaction {
            testDataGenerator.createUser()
        }
        val village = transactionManager.doInTransaction {
            villageRepository.listAll().shuffled()[0]
        }

        val testClient = getTestClient(user)
        val registerParams = RegisterFavoriteVillageParams.newBuilder()
            .setVillageId(village.id)
            .build()
        testClient.request("/registerFavoriteVillage", registerParams)

        val unregisterParams = UnregisterFavoriteVillageParams.newBuilder()
            .setVillageId(village.id)
            .build()
        testClient.request("/unregisterFavoriteVillage", unregisterParams).apply {
            val result = getResult(UnregisterFavoriteVillageResult::class)
            Assert.assertEquals(village.id, result.village.id)
            Assert.assertEquals(villageService.getName(village), result.village.name)
            Assert.assertFalse(result.village.isFavoriteVillage)
        }
    }
}
