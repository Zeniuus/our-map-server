package route

import domain.village.repository.VillageRepository
import domain.village.service.VillageService
import org.junit.Assert
import org.junit.Test
import org.koin.test.inject
import ourMap.protocol.RegisterFavoriteVillageParams
import ourMap.protocol.RegisterFavoriteVillageResult

class RegisterFavoriteVillageTest : OurMapServerRouteTestBase() {
    private val villageRepository by inject<VillageRepository>()
    private val villageService by inject<VillageService>()

    @Test
    fun registerFavoriteVillageTest() = runRouteTest {
        val user = transactionManager.doInTransaction {
            testDataGenerator.createUser()
        }
        val village = transactionManager.doInTransaction {
            villageRepository.listAll().shuffled()[0]
        }

        val testClient = getTestClient(user)
        val params = RegisterFavoriteVillageParams.newBuilder()
            .setVillageId(village.id)
            .build()
        testClient.request("/registerFavoriteVillage", params).apply {
            val result = getResult(RegisterFavoriteVillageResult::class)
            Assert.assertEquals(village.id, result.village.id)
            Assert.assertEquals(villageService.getName(village), result.village.name)
            Assert.assertTrue(result.village.isFavoriteVillage)
        }
    }
}
