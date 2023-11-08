package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

typealias typeErrCallback = (e: Throwable) -> Unit
interface PostRepository {

    val data: LiveData<List<Post>>
    suspend fun getAll()
    suspend fun like(post: Post)
    suspend fun deleteLike(post: Post)
    suspend fun save(post: Post)
    suspend fun remove(post: Post)
}
