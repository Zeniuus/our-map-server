package util

import java.security.MessageDigest
import java.util.Base64

object Hashing {
    fun getHash(byteArray: ByteArray, length: Int? = null): String {
        val md = MessageDigest.getInstance("SHA-256")
        md.update(byteArray)
        return Base64.getEncoder().encodeToString(md.digest()).let {
            if (length != null) {
                it.take(length)
            } else {
                it
            }
        }
    }
}
