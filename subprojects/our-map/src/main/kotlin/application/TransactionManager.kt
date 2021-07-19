package application

interface TransactionManager {
    fun <T> doInTransaction(block: () -> T): T

    /**
     * 테스트용 메소드.
     */
    fun doAndRollback(block: () -> Any)
}
