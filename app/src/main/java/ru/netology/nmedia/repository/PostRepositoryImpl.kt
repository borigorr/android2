package ru.netology.nmedia.repository

import androidx.lifecycle.*
import ru.netology.nmedia.api.*
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toDto
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.exceptions.HttpErrorException
import java.lang.Exception
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class PostRepositoryImpl(private val dao: PostDao) : PostRepository {

    override val data = dao.getAll().map(List<PostEntity>::toDto)
    override suspend fun getAll(){
        val  response = PostApi.service.getAll()
        if (!response.isSuccessful) {
            throw HttpErrorException()
        }
        val body = response.body() ?: throw HttpErrorException()
        dao.insert(body.toEntity())
    }

    override suspend fun likeById(id: Long) {
        val response = PostApi.service.likeById(id)
        if (!response.isSuccessful) {
            throw HttpErrorException()
        }
        val post = response.body() ?: throw HttpErrorException()
        dao.insert(PostEntity.fromDto(post))
    }

    override suspend fun deleteLikeById(id: Long) {
        val response = PostApi.service.deleteLikeById(id)
        if (!response.isSuccessful) {
            throw HttpErrorException()
        }
        val post = response.body() ?: throw HttpErrorException()
        dao.insert(PostEntity.fromDto(post))
        dao.likeById(id)
    }

    override suspend fun save(post: Post) {
        val oldPost = data.value?.find { it.id == post.id } ?: throw Exception()
        dao.insert(PostEntity.fromDto(post))
        coroutineScope {
            async {
                val response = PostApi.service.save(post)
                if (!response.isSuccessful) {
                    dao.insert(PostEntity.fromDto(oldPost))
                    throw HttpErrorException()
                }
            }
        }
    }
    override suspend fun removeById(id: Long) {
        val response = PostApi.service.removeById(id)
        if (!response.isSuccessful) {
            throw  throw HttpErrorException()
        }
        dao.removeById(id)
    }
}
