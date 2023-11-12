package ru.netology.nmedia.repository

import androidx.lifecycle.*
import ru.netology.nmedia.api.*
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toDto
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.exceptions.HttpErrorException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import ru.netology.nmedia.dto.toEntity
import kotlin.Exception

class PostRepositoryImpl(private val dao: PostDao) : PostRepository {

    override val data = dao.getAll().map(List<PostEntity>::toDto)
    override suspend fun getAll() {
        val response = PostApi.service.getAll()

        if (!response.isSuccessful) {
            throw HttpErrorException()
        }
        val body = response.body() ?: throw HttpErrorException()
        dao.insert(body.toEntity())
    }

    override suspend fun like(post: Post) {
        val postEntity = dao.getById(post.id)
        if (postEntity.serverId == 0L) {
            return
        }
        dao.likeById(post.id)
        try {
            val response = PostApi.service.likeById(postEntity.serverId)
            if (!response.isSuccessful) {
                dao.likeById(post.id)
                throw HttpErrorException()
            }
            val post = response.body() ?: throw HttpErrorException()
            dao.update(post.toEntity().copy(id = post.id))
        } catch (e: Throwable) {
            dao.likeById(post.id)
            throw e
        }
    }

    override suspend fun deleteLike(post: Post) {
        dao.likeById(post.id)
        try {
            val response = PostApi.service.deleteLikeById(post.serverId)
            if (!response.isSuccessful) {
                dao.likeById(post.id)
                throw HttpErrorException()
            }
            val serverPost = response.body() ?: throw HttpErrorException()
            dao.update(serverPost.toEntity(post.id))
        } catch (e: Throwable) {
            dao.likeById(post.id)
            throw e
        }
    }

    override suspend fun save(post: Post) {
        if (post.id == 0L) {
            create(post)
            return
        }
        update(post)
    }

    private suspend fun create(post: Post) {
        val id = dao.insert(PostEntity.fromDto(post)) ?: throw Exception()
        coroutineScope {
            async {
                try {
                    val response = PostApi.service.save(post)
                    if (!response.isSuccessful) {
                        dao.removeById(id)
                        throw HttpErrorException()
                    }
                    val body = response.body()
                    if (body == null) {
                        dao.removeById(id)
                    } else {
                        dao.update(body.toEntity(id))
                    }
                } catch (e: Exception) {
                    dao.removeById(id)
                    throw HttpErrorException()
                }
            }
        }
    }

    private suspend fun update(post: Post) {
        val oldPost = data.value?.find { it.id == post.id } ?: throw Exception()
        dao.update(PostEntity.fromDto(post))
        try {
            val response = PostApi.service.save(post.copy(id = post.serverId))
            if (!response.isSuccessful) {
                dao.insert(PostEntity.fromDto(oldPost))
                throw HttpErrorException()
            }
        } catch (e: Throwable) {
            dao.insert(PostEntity.fromDto(oldPost))
            throw HttpErrorException()
        }
    }

    override suspend fun remove(post: Post) {
        dao.removeById(post.id)
        val response = PostApi.service.removeById(post.serverId)
        if (!response.isSuccessful) {
            throw throw HttpErrorException()
        }
    }
}
