package ru.netology.nmedia.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Post

@Entity(indices = [Index(value = ["serverId"], unique = true)])
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val author: String,
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val likes: Int = 0,
    val authorAvatar: String,
    val serverId: Long = 0,
) {
    fun toDto() = Post(id, author, content, published, likedByMe, likes, authorAvatar, null, serverId)

    companion object {
        fun fromDto(dto: Post) =
            PostEntity(
                author = dto.author,
                content = dto.content,
                published = dto.published,
                likedByMe = dto.likedByMe,
                likes = dto.likes,
                authorAvatar = dto.authorAvatar,
                serverId = dto.serverId,
            )
    }
}

fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(): List<PostEntity> = map(PostEntity::fromDto)