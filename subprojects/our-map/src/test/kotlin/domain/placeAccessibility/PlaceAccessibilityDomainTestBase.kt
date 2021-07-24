package domain.placeAccessibility

import OurMapIoCFactory.installCommonConfig
import application.TransactionManager
import domain.DomainTestBase
import org.junit.Rule
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject

open class PlaceAccessibilityDomainTestBase : KoinTest, DomainTestBase() {
    protected val transactionManager by inject<TransactionManager>()

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        installCommonConfig()
        modules(
            placeAccessibilityDomainModule,
        )
    }
}
