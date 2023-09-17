package ru.netology.nmedia.util

import android.widget.ImageView
import com.bumptech.glide.Glide
import ru.netology.nmedia.R

fun ImageView .load(url: String, circleCrop: Boolean = true) {
    var glide = Glide.with(this)
        .load(url)
        .timeout(10_000)
        .placeholder(R.drawable.ic_loading_100dp)
        .error(R.drawable.ic_error_100dp)
        if (circleCrop) {
            glide =  glide.circleCrop()
        }
        glide.into(this)
}