package domain.user.entity

data class User(
    val id: String,
    var nickname: String,
    var email: String,
    var encryptedPassword: String,
    var instagramId: String?
)
