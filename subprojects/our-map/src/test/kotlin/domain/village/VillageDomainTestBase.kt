package domain.village

import OurMapIoCFactory.installCommonConfig
import application.TransactionManager
import org.junit.Rule
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject

open class VillageDomainTestBase : KoinTest {
    protected val transactionManager by inject<TransactionManager>()

    protected open val koinModules = listOf(villageDomainModule)

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        installCommonConfig()
        modules(
            koinModules
        )
    }
}