package domain.village

import application.village.villageApplicationModule
import domain.DomainTestBase
import domain.village.repository.VillageRepository
import domain.village.service.UserFavoriteVillageService
import org.junit.Assert
import org.junit.Test
import org.koin.test.inject

class UserFavoriteVillageServiceTest : DomainTestBase() {
    override val koinModules = listOf(villageDomainModule, villageApplicationModule)

    private val villageRepository by inject<VillageRepository>()
    private val userFavoriteVillageService by inject<UserFavoriteVillageService>()

    @Test
    fun `정상적인 경우`() = transactionManager.doAndRollback {
        val user = testDataGenerator.createUser()
        val village = villageRepository.listAll().shuffled()[0]
        // TODO: 원래대로면 여기서 villageApplicationService.insertAll()을 했어야 하나,
        //       현재 nested transaction이 지원되지 않아서 문제가 발생하여 일시적으로 @Before을 사용했다.

        Assert.assertFalse(userFavoriteVillageService.isFavoriteVillage(user, village))

        // 멱등성 테스트
        val userFavoriteVillages = (1..5).map {
            userFavoriteVillageService.register(user, village)
        }
        Assert.assertEquals(1, userFavoriteVillages.map { it.id }.toSet().size)

        Assert.assertTrue(userFavoriteVillageService.isFavoriteVillage(user, village))

        // 멱등성 테스트
        repeat(5) {
            userFavoriteVillageService.unregister(user, village)
        }
        Assert.assertFalse(userFavoriteVillageService.isFavoriteVillage(user, village))
    }
}
