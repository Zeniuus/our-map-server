package domain.util

import at.favre.lib.crypto.bcrypt.BCrypt

object Bcrypt {
    private val bcrypt = BCrypt.withDefaults()
    private val verifier = BCrypt.verifyer()

    fun encrypt(value: String): String {
        return bcrypt.hashToString(8, value.toCharArray())
    }

    fun verify(inputPw: String, encryptedPw: String): Boolean {
        return verifier.verify(inputPw.toCharArray(), encryptedPw.toCharArray()).verified
    }
}
