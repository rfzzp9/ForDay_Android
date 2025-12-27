package com.forday.app.remote.api.service

import com.forday.app.remote.model.response.ExampleReponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ExampleApi {

    @GET("")
    suspend fun getExampleData(
        @Query("example1") example1: String,
        @Query("example2") example2: String,
    ): ExampleReponse

}