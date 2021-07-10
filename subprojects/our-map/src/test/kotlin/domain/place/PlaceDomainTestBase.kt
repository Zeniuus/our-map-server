package domain.place

import OurMapIoCFactory.installCommonConfig
import application.TransactionManager
import org.junit.Rule
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject

open class PlaceDomainTestBase : KoinTest {
    protected val transactionManager by inject<TransactionManager>()

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        installCommonConfig()
        modules(
            placeDomainModule,
        )
    }
}
