package ru.netology.nmedia.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.create
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.internal.EMPTY_REQUEST
import ru.netology.nmedia.dto.Post
import java.io.IOException
import java.util.concurrent.TimeUnit


class PostRepositoryImpl: PostRepository {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Post>>() {}

    companion object {
        private const val BASE_URL = "http://192.168.88.93:9999"
        private val jsonType = "application/json".toMediaType()
    }


    override fun getAllAsync(callback: PostRepository.GetAllCallback) {
        val request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .build()
        client.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    var result = response.body?.string()
                    if (result == null) {
                        callback.onError(RuntimeException("body is null"))
                        return
                    }
                    try {
                        var list = gson.fromJson(result, typeToken)
                        callback.onSuccess(list)
                    } catch (e: Exception) {
                        callback.onError(e)
                    }

                }

            })
    }
    override fun getAll(): List<Post> {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .build()

        return client.newCall(request)
            .execute()
            .let { it.body?.string() ?: throw RuntimeException("body is null") }
            .let {
                gson.fromJson(it, typeToken.type)
            }
    }

    override fun likeById(id: Long) {

        val request: Request = Request.Builder()
            .post(EMPTY_REQUEST)
            .url("${BASE_URL}/api/posts/${id}/likes")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }

    override fun deleteLikeById(id: Long) {

        val request: Request = Request.Builder()
            .delete(EMPTY_REQUEST)
            .url("${BASE_URL}/api/posts/${id}/likes")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }

    override fun saveAsync(post: Post, errorCallback: typeErrCallback) {
        val request: Request = Request.Builder()
            .post(gson.toJson(post.copy(author = "Me")).toRequestBody(jsonType))
            .url("${BASE_URL}/api/slow/posts")
            .build()

        client.newCall(request)
            .enqueue(object :Callback {
                override fun onFailure(call: Call, e: IOException) {
                    errorCallback(e)
                }

                override fun onResponse(call: Call, response: Response) {
                }

            })
    }

    override fun deleteLikeByIdAsync(id: Long, errorCallback: typeErrCallback) {
        val request: Request = Request.Builder()
            .delete(EMPTY_REQUEST)
            .url("${BASE_URL}/api/posts/${id}/likes")
            .build()
        client.newCall(request)
            .enqueue(object: Callback {
                override fun onFailure(call: Call, e: IOException) {
                    errorCallback(e)
                }

                override fun onResponse(call: Call, response: Response) {

                }
            })

    }

    override fun likeByIdAsync(id: Long,  errorCallback: (e: Exception) -> Unit) {
        val request: Request = Request.Builder()
            .post(EMPTY_REQUEST)
            .url("${BASE_URL}/api/posts/${id}/likes")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    errorCallback(e)
                }
                override fun onResponse(call: Call, response: Response) {

                }
            })
    }

    override fun  removeByIdAsync(id: Long, errorCallback: (e: Exception) -> Unit) {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/slow/posts/$id")
            .build()

        client.newCall(request)
            .enqueue(object : Callback{
                override fun onFailure(call: Call, e: IOException) {
                    errorCallback(e)
                }

                override fun onResponse(call: Call, response: Response) {
                }

            })
    }

    override fun save(post: Post) {
        val request: Request = Request.Builder()
            .post(gson.toJson(post.copy(author = "Me")).toRequestBody(jsonType))
            .url("${BASE_URL}/api/slow/posts")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }

    override fun removeById(id: Long) {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/slow/posts/$id")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }
}
