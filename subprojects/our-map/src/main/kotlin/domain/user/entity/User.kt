package domain.user.entity

import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class User(
    @Id
    @Column(nullable = false, length = 36)
    val id: String,
    @Column(nullable = false, length = 32)
    var nickname: String,
    @Column(nullable = false, length = 64)
    var encryptedPassword: String,
    @Column(nullable = true, length = 32)
    var instagramId: String?,
    @Column(nullable = false)
    val createdAt: Instant,
)
