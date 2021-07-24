package domain

open class DomainTestBase {
    init {
        MySQLContainer.startOnce()
    }
}
