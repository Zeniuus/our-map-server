package domain.place

import domain.DomainTestBase
import domain.place.service.SearchPlaceService
import domain.util.Length
import domain.util.Location
import domain.village.repository.EupMyeonDongRepository
import domain.village.repository.SiGunGuRepository
import org.junit.Assert
import org.junit.Test
import org.koin.test.inject

class SearchPlaceServiceTest : DomainTestBase() {
    override val koinModules = listOf(placeDomainModule)

    private val siGunGuRepository by inject<SiGunGuRepository>()
    private val eupMyeonDongRepository by inject<EupMyeonDongRepository>()
    private val searchPlaceService by inject<SearchPlaceService>()

    @Test
    fun `getSQLSearchTextRegex 테스트`() {
        Assert.assertEquals(".타벅스|스.벅스|스타.스|스타벅.", searchPlaceService.getSQLSearchTextRegex("스타벅스")) // 세 글자 이상이면 변환
        Assert.assertEquals("CGV야탑", searchPlaceService.getSQLSearchTextRegex("CGV야탑")) // 한글 두 글자 이하는 변환 X
        Assert.assertEquals("CGV ?야탑", searchPlaceService.getSQLSearchTextRegex("CGV 야탑")) // 스페이스는 검색 결과에 영향을 미치지 않도록 변환
        Assert.assertEquals("CGV ?.탑역|CGV ?야.역|CGV ?야탑.", searchPlaceService.getSQLSearchTextRegex("CGV 야탑역")) // 한글만 변환
    }

    @Test
    fun `검색이 잘 되어야 한다`() = transactionManager.doAndRollback {
        val result1 = searchPlaceService.searchPlaces(SearchPlaceService.SearchOption(
            searchText = "수환",
            location = Location(0.0, 0.0),
        ))
        Assert.assertEquals(0, result1.size)

        val place1 = testDataGenerator.createBuildingAndPlace(placeName = "D타워 장소1")

        val result2 = searchPlaceService.searchPlaces(SearchPlaceService.SearchOption(
            searchText = "타워",
            location = Location(0.0, 0.0),
        ))
        Assert.assertEquals(1, result2.size)
        Assert.assertEquals(place1.id, result2[0].place.id)

        val result3 = searchPlaceService.searchPlaces(SearchPlaceService.SearchOption(
            searchText = "워타",
            location = Location(0.0, 0.0),
        ))
        Assert.assertEquals(0, result3.size)

        val result4 = searchPlaceService.searchPlaces(SearchPlaceService.SearchOption(
            searchText = "타",
            location = Location(0.0, 0.0),
        )) // 한글자로는 검색이 안 되어야 한다.
        Assert.assertEquals(0, result4.size)

        val result5 = searchPlaceService.searchPlaces(SearchPlaceService.SearchOption(
            searchText = "D타워장소",
            location = Location(0.0, 0.0),
        )) // 띄워쓰기를 안 하고 검색해도 검색이 잘 돼야 한다.
        Assert.assertEquals(1, result5.size)
        Assert.assertEquals(place1.id, result5[0].place.id)

        val result6 = searchPlaceService.searchPlaces(SearchPlaceService.SearchOption(
            searchText = "디타워장소",
            location = Location(0.0, 0.0),
        )) // 한글 한 글자는 틀려도 찾아준다.
        Assert.assertEquals(1, result6.size)
        Assert.assertEquals(place1.id, result6[0].place.id)

        val place2 = testDataGenerator.createBuildingAndPlace(placeName = "D타워장소1")
        val result7 = searchPlaceService.searchPlaces(SearchPlaceService.SearchOption(
            searchText = "디타워 장소",
            location = Location(0.0, 0.0),
        )) // 스페이스를 추가해서 검색해도 찾아준다.
        Assert.assertEquals(2, result7.size)
        Assert.assertEquals(setOf(place1.id, place2.id), result7.map { it.place.id }.toSet())
    }

    @Test
    fun `가까운 장소가 먼저 노출되어야 한다`() = transactionManager.doAndRollback {
        val result1 = searchPlaceService.searchPlaces(SearchPlaceService.SearchOption(
            searchText = "수환",
            location = Location(0.0, 0.0),
        ))
        Assert.assertEquals(0, result1.size)

        val nearPlace = testDataGenerator.createBuildingAndPlace(
            placeName = "D타워 장소1",
            location = Location(0.05, 0.05)
        )
        val farPlace = testDataGenerator.createBuildingAndPlace(
            placeName = "D타워 장소2",
            location = Location(0.1, 0.1)
        )

        val result = searchPlaceService.searchPlaces(SearchPlaceService.SearchOption(
            searchText = "D타워",
            location = Location(0.0, 0.0),
        ))
        Assert.assertEquals(2, result.size)
        Assert.assertEquals(nearPlace.id, result[0].place.id)
        Assert.assertEquals(farPlace.id, result[1].place.id)
    }

    @Test
    fun `SearchOption 테스트`() = transactionManager.doAndRollback {
        val (targetSiGunGu, otherSiGunGu) = siGunGuRepository.listAll().shuffled()
        val (targetEupMyeonDong, otherEupMyeonDong) = eupMyeonDongRepository.listAll().shuffled()

        testDataGenerator.createBuildingAndPlace(
            placeName = "D타워 장소1",
            location = Location(0.0, 0.0),
            siGunGuId = targetSiGunGu.id,
            eupMyeonDongId = targetEupMyeonDong.id,
        )

        // 거리 필터 테스트
        run {
            val result1 = searchPlaceService.searchPlaces(SearchPlaceService.SearchOption(
                searchText = "D타워",
                location = Location(0.0, 0.0),
                maxDistance = Length(100),
            ))
            Assert.assertEquals(1, result1.size)
            val result2 = searchPlaceService.searchPlaces(SearchPlaceService.SearchOption(
                searchText = "D타워",
                location = Location(0.5, 0.5), // 먼 장소
                maxDistance = Length(100),
            ))
            Assert.assertEquals(0, result2.size) // 거리 필터에 걸려야 한다.
        }

        // 시군구 필터 테스트
        run {
            val result1 = searchPlaceService.searchPlaces(SearchPlaceService.SearchOption(
                searchText = "D타워",
                location = Location(0.0, 0.0),
                siGunGu = targetSiGunGu,
            ))
            Assert.assertEquals(1, result1.size)
            val result2 = searchPlaceService.searchPlaces(SearchPlaceService.SearchOption(
                searchText = "D타워",
                location = Location(0.0, 0.0),
                siGunGu = otherSiGunGu,
            ))
            Assert.assertEquals(0, result2.size) // 시군구 필터에 걸려야 한다.
        }

        // 읍면동 필터 테스트
        run {
            val result1 = searchPlaceService.searchPlaces(SearchPlaceService.SearchOption(
                searchText = "D타워",
                location = Location(0.0, 0.0),
                eupMyeonDong = targetEupMyeonDong,
            ))
            Assert.assertEquals(1, result1.size)
            val result2 = searchPlaceService.searchPlaces(SearchPlaceService.SearchOption(
                searchText = "D타워",
                location = Location(0.0, 0.0),
                eupMyeonDong = otherEupMyeonDong,
            ))
            Assert.assertEquals(0, result2.size) // 읍면동 필터에 걸려야 한다.
        }

        // 복합적인 필터를 걸어도 검색이 잘 되어야 한다.
        val result = searchPlaceService.searchPlaces(SearchPlaceService.SearchOption(
            searchText = "D타워",
            location = Location(0.0, 0.0),
            maxDistance = Length(100),
            siGunGu = targetSiGunGu,
            eupMyeonDong = targetEupMyeonDong,
        ))
        Assert.assertEquals(1, result.size)
    }

    @Test
    fun `현재 위치를 넣어야만 distance가 존재한다`() = transactionManager.doAndRollback {
        val place1 = testDataGenerator.createBuildingAndPlace(placeName = "D타워 장소1")

        val result1 = searchPlaceService.searchPlaces(SearchPlaceService.SearchOption(
            searchText = "타워",
            location = Location(0.0, 0.0),
        ))
        Assert.assertEquals(1, result1.size)
        Assert.assertEquals(place1.id, result1[0].place.id)
        Assert.assertNotNull(result1[0].distance)

        val result2 = searchPlaceService.searchPlaces(SearchPlaceService.SearchOption(
            searchText = "타워",
            location = null,
        ))
        Assert.assertEquals(1, result2.size)
        Assert.assertEquals(place1.id, result2[0].place.id)
        Assert.assertNull(result2[0].distance)
    }
}
