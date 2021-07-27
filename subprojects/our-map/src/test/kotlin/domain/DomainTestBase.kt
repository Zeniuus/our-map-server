package domain

import OurMapIoCFactory.installCommonConfig
import TestDataGenerator
import application.TransactionManager
import org.junit.Rule
import org.koin.core.module.Module
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject

open class DomainTestBase : KoinTest {
    protected val testDataGenerator = TestDataGenerator()
    protected val transactionManager by inject<TransactionManager>()

    protected open val koinModules = emptyList<Module>()
    @get:Rule
    val koinTestRule = KoinTestRule.create {
        installCommonConfig()
        modules(koinModules)
    }

    init {
        MySQLContainer.startOnce()
    }
}
