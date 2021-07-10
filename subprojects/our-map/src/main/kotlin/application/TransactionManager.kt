package application

interface TransactionManager {
    fun <T> doInTransaction(block: () -> T): T
}
