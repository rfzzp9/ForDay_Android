package com.forday.app.remote.api

import com.forday.app.remote.model.response.ApiResponse
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.reflect.Type

internal class ApiResponseCallAdapter<R>(
    private val successType: Type
) : CallAdapter<R, Call<ApiResponse<R>>> {
    override fun adapt(call: Call<R>): Call<ApiResponse<R>> = ApiResponseCall(call, successType)
    override fun responseType(): Type = successType
}

private class ApiResponseCall<R>(
    private val delegate: Call<R>,
    private val successType: Type
): Call<ApiResponse<R>> {

    override fun enqueue(callback: Callback<ApiResponse<R>>) = delegate.enqueue(
        object : Callback<R> {

            override fun onResponse(call: Call<R>, response: Response<R>) {
                callback.onResponse(this@ApiResponseCall, Response.success(response.toApiResponse()))
            }

            private fun Response<R>.toApiResponse(): ApiResponse<R> {
                // Http error response (4xx ~ 5xx) : 응답 실패
                if (!isSuccessful) {
                    val errorBody = errorBody()?.string().orEmpty()
                    return ApiResponse.Failure.HttpError(
                        code = code(),
                        message = message(),
                        body = errorBody
                    )
                }

                // Http success response (200) : 응답 성공
                body()?.let { body ->
                    return ApiResponse.successOf(body)
                }

                // successType이 Unit인 경우 Body가 존재하지 않더라도 성공으로 간주합니다.
                return if (successType == Unit::class.java) {
                    @Suppress("UNCHECKED_CAST")
                    ApiResponse.successOf(Unit as R)
                } else {
                    ApiResponse.Failure.UnknownApiError(
                        IllegalStateException(
                            "Response code가 ${code()}이지만 body가 null입니다.\n" +
                                    "만약 Response body가 null이길 원한다면 Unit을 반환하도록 API 메서드를 정의해주세요:\n\n" +
                                    "interface MonkeyApi {\n" +
                                    "   @POST\n" +
                                    "   fun postSomething(): ApiResponse<Unit>\n" +
                                    "}"
                        )
                    )
                }

            }

            override fun onFailure(call: Call<R>, throwable: Throwable) {
                val error = if (throwable is IOException) {
                    ApiResponse.Failure.NetworkError(throwable)
                } else {
                    ApiResponse.Failure.UnknownApiError(throwable)
                }
                callback.onResponse(this@ApiResponseCall, Response.success(error))
            }

        }
    )

    override fun clone(): Call<ApiResponse<R>> = ApiResponseCall(delegate.clone(), successType)

    override fun execute(): Response<ApiResponse<R>> =
        throw UnsupportedOperationException("이 어댑터는 동기 작업을 지원하지 않습니다. 대신 비동기 작업을 지원하는 enqueue를 사용해주세요.")

    override fun isExecuted(): Boolean = delegate.isExecuted

    override fun cancel() = delegate.cancel()

    override fun isCanceled(): Boolean = delegate.isCanceled

    override fun request(): Request = delegate.request()

    override fun timeout(): Timeout = delegate.timeout()

}
