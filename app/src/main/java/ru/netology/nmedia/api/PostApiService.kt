package ru.netology.nmedia.api

import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Body
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.ServerPost
import java.util.concurrent.TimeUnit


private const val BASE_URL = "${BuildConfig.BASE_URL}/api/"

private val client = OkHttpClient.Builder()
    .connectTimeout(30, TimeUnit.SECONDS)
    .build()
private val gson = GsonConverterFactory.create()

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .client(client)
    .addConverterFactory(gson)
    .build()

interface PostApiService {
    @GET("slow/posts")
    suspend fun getAll(): Response<List<ServerPost>>

    @POST("posts/{id}/likes")
    suspend fun likeById(@Path("id") id: Long): Response<ServerPost>

    @DELETE("posts/{id}/likes")
    suspend fun deleteLikeById(@Path("id") id: Long): Response<ServerPost>

    @DELETE("slow/posts/{id}")
    suspend fun  removeById(@Path("id") id: Long): Response<Unit>

    @POST("posts")
    suspend fun  save(@Body post: Post): Response<ServerPost>
}


object PostApi {
    val service: PostApiService by lazy {
        retrofit.create()
    }
}