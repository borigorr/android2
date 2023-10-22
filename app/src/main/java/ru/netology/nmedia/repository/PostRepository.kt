package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

typealias typeErrCallback = (e: Throwable) -> Unit
interface PostRepository {

    val data: LiveData<List<Post>>
    suspend fun getAll()
    suspend fun likeById(id: Long)
    suspend fun deleteLikeById(id: Long)
    suspend fun save(post: Post)
    suspend fun removeById(id: Long)
}
