package route

import application.village.VillageApplicationService
import domain.village.repository.EupMyeonDongRepository
import domain.village.repository.VillageRepository
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.koin.test.inject
import ourMap.protocol.ListVillageDropdownItemsParams
import ourMap.protocol.ListVillageDropdownItemsResult

class ListVillageDropdownItemsTest : OurMapServerRouteTestBase() {
    private val villageRepository by inject<VillageRepository>()
    private val eupMyeonDongRepository by inject<EupMyeonDongRepository>()
    private val villageApplicationService by inject<VillageApplicationService>()

    @Before
    fun setUp() {
        transactionManager.doInTransaction {
            villageApplicationService.insertAll()
        }
    }

    @Test
    fun testListVillageDropdownItems() = runRouteTest {
        val user = transactionManager.doInTransaction {
            testDataGenerator.createUser()
        }
        getTestClient(user).request("/listVillageDropdownItems", ListVillageDropdownItemsParams.getDefaultInstance()).apply {
            val result = getResult(ListVillageDropdownItemsResult::class)
            val (villages, eupMyeonDongById) = transactionManager.doInTransaction {
                Pair(villageRepository.listAll(), eupMyeonDongRepository.listAll().associateBy { it.id })
            }
            Assert.assertEquals(villages.map { it.id }.toSet(), result.villageDropdownItemsList.map { it.villageId }.toSet())
            result.villageDropdownItemsList.forEach { dropdownItem ->
                val sameVillage = villages.find { it.id == dropdownItem.villageId }!!
                val eupMyeonDong = eupMyeonDongById[sameVillage.eupMyeonDongId]!!
                Assert.assertEquals("${eupMyeonDong.siGunGu.name} ${eupMyeonDong.name}", dropdownItem.villageName)
            }
        }
    }
}
