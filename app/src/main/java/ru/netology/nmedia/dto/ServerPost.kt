package ru.netology.nmedia.dto

import ru.netology.nmedia.entity.PostEntity

data class ServerPost(
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val likes: Int = 0,
    val authorAvatar: String,
    val attachment: ServerPostAttachment? = null,
) {
    fun toEntity(postId: Long = 0) = PostEntity(
        id = postId,
        author = author,
        content = content,
        published = published,
        likedByMe = likedByMe,
        likes = likes,
        authorAvatar = authorAvatar,
        serverId = id
    )
}

data class ServerPostAttachment(
    val url: String,
    val description: String,
    val type: String,
)

fun List<ServerPost>.toEntity(): List<PostEntity> = map(ServerPost::toEntity)