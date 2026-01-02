package com.forday.app.remote.model.response

sealed interface ApiResponse<out T> {
    data class Success<T>(val data: T) : ApiResponse<T>

    sealed interface Failure : ApiResponse<Nothing> {

        data class HttpError(val code: Int, val message: String, val body: String) : Failure

        data class NetworkError(val throwable: Throwable) : Failure

        data class UnknownApiError(val throwable: Throwable) : Failure

    }

    companion object {
        fun <R> successOf(result: R): ApiResponse<R> = Success(result)
    }

}