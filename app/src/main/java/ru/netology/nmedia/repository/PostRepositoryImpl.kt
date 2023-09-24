package ru.netology.nmedia.repository

import retrofit2.Callback
import retrofit2.Call
import ru.netology.nmedia.api.PostApi
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.exceptions.HttpErrorException


class PostRepositoryImpl : PostRepository {

    override fun getAllAsync(callback: PostRepository.GetAllCallback) {
        PostApi.service.getAll().enqueue(object : retrofit2.Callback<List<Post>> {

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                callback.onError(t)
            }

            override fun onResponse(
                call: Call<List<Post>>,
                response: retrofit2.Response<List<Post>>
            ) {
                if (!response.isSuccessful) {
                    callback.onError(RuntimeException(response.message()))
                    return
                }
                val posts = response.body() ?: emptyList()
                callback.onSuccess(posts)
            }

        })
    }

    override fun getAll(): List<Post> {
        return PostApi.service.getAll()
            .execute()
            .let { it.body() ?: emptyList() }
    }

    override fun likeById(id: Long) {
        PostApi.service.getAll().execute()
    }

    override fun deleteLikeById(id: Long) {
        PostApi.service.deleteLikeById(id).execute()
    }

    override fun saveAsync(post: Post, errorCallback: typeErrCallback) {
        PostApi.service.save(post).enqueue(object : Callback<Post> {

            override fun onFailure(call: Call<Post>, t: Throwable) {
                errorCallback(t)
            }

            override fun onResponse(call: Call<Post>, response: retrofit2.Response<Post>) {
                if (!response.isSuccessful) {
                    errorCallback(HttpErrorException())
                }
            }
        })
    }

    override fun deleteLikeByIdAsync(id: Long, errorCallback: typeErrCallback) {
        PostApi.service.deleteLikeById(id).enqueue(object : Callback<Post> {
            override fun onFailure(call: Call<Post>, t: Throwable) {
                errorCallback(t)
            }

            override fun onResponse(call: Call<Post>, response: retrofit2.Response<Post>) {
                if (!response.isSuccessful) {
                    errorCallback(HttpErrorException())
                }
            }
        })

    }

    override fun likeByIdAsync(id: Long, errorCallback: (e: Throwable) -> Unit) {
        PostApi.service.likeById(id).enqueue(object : Callback<Post> {
            override fun onFailure(call: Call<Post>, t: Throwable) {
                errorCallback(t)
            }

            override fun onResponse(
                call: Call<Post>,
                response: retrofit2.Response<Post>
            ) {
                if (!response.isSuccessful) {
                    errorCallback(HttpErrorException())
                }
            }
        })
    }

    override fun removeByIdAsync(id: Long, errorCallback: (t: Throwable) -> Unit) {
        PostApi.service.removeById(id)
            .enqueue(object : Callback<Unit> {

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    errorCallback(t)
                }

                override fun onResponse(call: Call<Unit>, response: retrofit2.Response<Unit>) {
                    if (!response.isSuccessful) {
                        errorCallback(HttpErrorException())
                    }
                }

            })
    }

    override fun save(post: Post) {
        PostApi.service.save(post).execute()
    }

    override fun removeById(id: Long) {
        PostApi.service.removeById(id).execute()

    }
}
