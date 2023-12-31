package ru.netology.nmedia.api

import okhttp3.OkHttpClient
import retrofit2.Call
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
    fun getAll(): Call<List<Post>>

    @POST("slow/posts/{id}/likes")
    fun likeById(@Path("id") id: Long): Call<Post>

    @DELETE("slow/posts/{id}/likes")
    fun deleteLikeById(@Path("id") id: Long): Call<Post>

    @DELETE("slow/posts/{id}")
    fun  removeById(@Path("id") id: Long): Call<Unit>

    @POST("posts")
    fun  save(@Body post: Post): Call<Post>
}


object PostApi {
    val service: PostApiService by lazy {
        retrofit.create()
    }
}