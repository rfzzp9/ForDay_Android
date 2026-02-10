package com.forday.app.core.util

import org.json.JSONObject
import retrofit2.HttpException
import timber.log.Timber

fun HttpException.logAndExtractServerMessage(tag: String? = null): String? {
    val errorBody = runCatching { response()?.errorBody()?.string() }.getOrNull()

    Timber.e(
        this,
        "%sHttpException code=%d message=%s body=%s",
        if (tag.isNullOrBlank()) "" else "[$tag] ",
        code(),
        message(),
        errorBody
    )

    if (errorBody.isNullOrBlank()) return null

    return runCatching {
        val root = JSONObject(errorBody)
        val data = root.optJSONObject("data")
        data?.optString("message")
    }.getOrNull()
}
