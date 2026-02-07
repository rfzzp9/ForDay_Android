package com.forday.app.core.util

import com.app.forday.R

object ImageCodeMapper {

    private val imageCodeMap = mapOf(
        "drawing.png" to R.drawable.hobbycard_drawing,
        "gym.png" to R.drawable.hobbycard_gym,
        "reading.png" to R.drawable.hobbycard_reading,
        "music.png" to R.drawable.hobbycard_listeningmusic,
        "running.png" to R.drawable.hobbycard_running,
        "cooking.png" to R.drawable.hobbycard_cooking,
        "cafe.png" to R.drawable.hobbycard_cafe,
        "movie.png" to R.drawable.hobbycard_watchingmovie,
        "photo.png" to R.drawable.hobbycard_pictures,
        "writing.png" to R.drawable.hobbycard_writing
    )

    fun getDrawableResId(imageCode: String): Int {
        return imageCodeMap[imageCode] ?: R.drawable.hobbycard_gym
    }
}