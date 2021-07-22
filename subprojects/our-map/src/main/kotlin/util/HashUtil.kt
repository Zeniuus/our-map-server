package util

import java.security.MessageDigest

object HashUtil {
    fun getHash(byteArray: ByteArray): String {
        val md = MessageDigest.getInstance("SHA-256")
        md.update(byteArray)
        return md.digest().toString()
    }
}
