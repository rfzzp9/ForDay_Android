package com.forday.app.core.util

import com.dayn.forday.R

object ImageCodeMapper {

    private val imageCodeMap = mapOf(
        "drawing.png" to R.drawable.hobby_draw,
        "gym.png" to R.drawable.hobby_health,
        "reading.png" to R.drawable.hobby_book,
        "music.png" to R.drawable.hobby_music,
        "running.png" to R.drawable.hobby_running,
        "cooking.png" to R.drawable.hobby_cook,
        "cafe.png" to R.drawable.hobby_cafe,
        "movie.png" to R.drawable.hobby_movie,
        "photo.png" to R.drawable.hobby_camera,
        "writing.png" to R.drawable.hobby_pencil,
        "DRAWING_ICON" to R.drawable.hobby_draw,
        "GYM_ICON" to R.drawable.hobby_health,
        "READING_ICON" to R.drawable.hobby_book,
        "MUSIC_ICON" to R.drawable.hobby_music,
        "RUNNING_ICON" to R.drawable.hobby_running,
        "COOKING_ICON" to R.drawable.hobby_cook,
        "CAFE_ICON" to R.drawable.hobby_cafe,
        "MOVIE_ICON" to R.drawable.hobby_movie,
        "PHOTO_ICON" to R.drawable.hobby_camera,
        "WRITING_ICON" to R.drawable.hobby_pencil,
        "DEFAULT_ICON" to R.drawable.ic_etc_hobby
    )

    fun getDrawableResId(imageCode: String): Int {
        return imageCodeMap[imageCode] ?: R.drawable.hobbycard_gym
    }
}
