package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

typealias typeErrCallback = (e: Exception) -> Unit
interface PostRepository {
    fun getAll(): List<Post>
    fun likeById(id: Long)
    fun save(post: Post)
    fun removeById(id: Long)
    fun deleteLikeById(id: Long)

    fun saveAsync(post: Post, errorCallback: typeErrCallback)

    fun deleteLikeByIdAsync(id: Long, errorCallback: typeErrCallback)

    fun removeByIdAsync(id: Long, errorCallback: typeErrCallback)

    fun likeByIdAsync(id: Long, errorCallback: typeErrCallback)

    fun getAllAsync(callback: GetAllCallback)
    interface GetAllCallback {
        fun onSuccess(posts: List<Post>)
        fun onError(e: Exception)
    }
}
