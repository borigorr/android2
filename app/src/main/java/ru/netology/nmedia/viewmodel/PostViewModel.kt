package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.*
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.*
import ru.netology.nmedia.util.SingleLiveEvent
import kotlin.Exception
import kotlin.concurrent.thread

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    likedByMe = false,
    likes = 0,
    published = "",
    authorAvatar = "",
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    // упрощённый вариант
    private val repository: PostRepository = PostRepositoryImpl()
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data
    val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        loadPosts()
    }

    fun loadPosts() {
        // Начинаем загрузку
        _data.postValue(FeedModel(loading = true, posts = _data.value?.posts ?: emptyList()))
        repository.getAllAsync(object : PostRepository.GetAllCallback {
            override fun onSuccess(posts: List<Post>) {
                _data.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true))
            }
        })
    }

    fun save() {
        edited.value?.let {
            repository.saveAsync(it) {

            }
            _postCreated.postValue(Unit)
        }
        edited.value = empty
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun likeById(id: Long) {
        val post = _data.value?.posts?.firstOrNull{ it.id == id }
        if (post == null) {
            loadPosts()
            return
        }
        val oldLikeByMe = post.likedByMe
        val newPost = post.copy(likedByMe = !oldLikeByMe, likes = if (oldLikeByMe) post.likes - 1 else post.likes + 1)
        setPost(newPost)
        if (oldLikeByMe) {
            repository.deleteLikeByIdAsync(id) {
                val newPost = newPost.copy(likedByMe = true, likes =  post.likes + 1)
                setPost(newPost)
            }
        } else {
            repository.likeByIdAsync(id) {
                val newPost = newPost.copy(likedByMe = false, likes =  post.likes - 1)
                setPost(newPost)
            }
        }
    }


    private fun setPost(post: Post) {
        _data.postValue(
            _data.value?.posts?.let {
                it.map {
                    if (post.id == it.id) {
                        post
                    } else {
                        it
                    }
                }
            }?.let { _data.value?.copy(posts = it) }
        )
    }

    fun removeById(id: Long) {
        val old = _data.value?.posts.orEmpty()
        _data.postValue(
            _data.value?.copy(posts = _data.value?.posts.orEmpty()
                .filter { it.id != id }
            )
        )
        repository.removeByIdAsync(id) {
            _data.postValue(_data.value?.copy(posts = old))
        }
    }
}
