package util

import org.junit.Assert
import org.junit.Test

class HashingTest {
    @Test
    fun `멱등성 테스트`() {
        Assert.assertEquals(1, (1..10).map { Hashing.getHash("someValue".toByteArray()) }.toSet().size)
    }
}
