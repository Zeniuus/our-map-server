package util

import java.security.MessageDigest

object HashUtil {
    // TODO: 지금 해시된 값에 멱등성이 없는 것 같다.
    fun getHash(byteArray: ByteArray): String {
        val md = MessageDigest.getInstance("SHA-256")
        md.update(byteArray)
        return md.digest().toString()
    }
}
