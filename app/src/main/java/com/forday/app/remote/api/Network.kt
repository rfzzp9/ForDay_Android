package com.forday.app.remote.api

import retrofit2.Retrofit

class Network(
    private val retrofit: Retrofit.Builder,
) {

    fun <T> create(baseUrl: String, service: Class<T>): T =
        retrofit
            .baseUrl(baseUrl)
            .build()
            .create(service)

    inline fun <reified T> create(baseUrl: String): T {
        return create(baseUrl, T::class.java)
    }
}