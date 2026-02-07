package com.forday.app.remote.api.interceptor

import com.forday.app.core.datastore.UserLocalDataSource
import com.forday.app.core.session.AuthEvent
import com.forday.app.core.session.AuthEventBus
import com.forday.app.remote.api.service.TokenApi
import com.forday.app.remote.model.request.RefreshRequest
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import java.net.HttpURLConnection
import java.nio.charset.Charset
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenInterceptor @Inject constructor(
    private val tokenApi: TokenApi,
    private val tokenProvider: TokenProvider,
    private val authEventBus: AuthEventBus,
    private val userLocalDataSource: UserLocalDataSource,
) : Interceptor {

    private val mutex = Mutex()

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        Timber.e("###########22######╔════════════════════════════════════════════════════════════")
        Timber.e("###########22######║ REQUEST")
        Timber.e("###########22######╠════════════════════════════════════════════════════════════")
        Timber.e("###########22######║ URL: ${originalRequest.url}")
        Timber.e("###########22######║ METHOD: ${originalRequest.method}")
        Timber.e("###########22######║ HEADERS:")
        val accessToken = tokenProvider.getAccessToken()
        Timber.e("@###@!@$@$@A#@# $accessToken")
        val authedRequest = originalRequest.newBuilder().apply {
            if (!accessToken.isNullOrBlank()) header("Authorization", "Bearer $accessToken")
        }.build()
        Timber.e("###########22###### RefreshToken from provider: ${tokenProvider.getRefreshToken()}")
        val response = chain.proceed(authedRequest)
        Timber.e("###########22######╔════════════════════════════════════════════════════════════")
        Timber.e("###########22######║ RESPONSE"+accessToken)
        Timber.e("###########22######╠════════════════════════════════════════════════════════════")
        Timber.e("###########22######║ encodedPath: ${originalRequest.url.encodedPath}")
        Timber.e("###########22######║ CODE: ${response.code}")
        Timber.e("###########22######║ MESSAGE: ${response.message}")
        Timber.e("###########22######║ HEADERS:")

        val responseBody = response.body
        val source = responseBody?.source()
        source?.request(Long.MAX_VALUE)
        val buffer = source?.buffer
        val charset = responseBody?.contentType()?.charset(Charset.forName("UTF-8")) ?: Charset.forName("UTF-8")
        val bodyString = buffer?.clone()?.readString(charset) ?: ""

        Timber.e("###########22######║ BODY: $bodyString")
        Timber.e("###########22######╚════════════════════════════════════════════════════════════")
        if (response.code != HttpURLConnection.HTTP_UNAUTHORIZED) {
            return response
        }

        val newAccess = runBlocking { refreshSafely(accessToken) }

        return if (newAccess.isNullOrBlank()) {
            response
        } else {
            response.close()
            val retried = originalRequest.newBuilder()
                .header("Authorization", "Bearer $newAccess")
                .build()
            chain.proceed(retried)
        }
    }

    private suspend fun refreshSafely(oldAccess: String?): String? {
        Timber.e("1############################### "+oldAccess)
        return mutex.lockAndGet {
            val latest = tokenProvider.getAccessToken()
            Timber.e("2############################### "+latest)
            Timber.e("2############################### "+tokenProvider.getRefreshToken())
            if (!latest.isNullOrBlank() && latest != oldAccess) return@lockAndGet latest
            Timber.e("3############################### "+tokenProvider.getRefreshToken())
            val refreshToken = tokenProvider.getRefreshToken()
            val rt = refreshToken ?: return@lockAndGet null
            try {
                Timber.e("4############################### "+rt)
                val res = tokenApi.refreshToken(RefreshRequest(rt))
                Timber.e("4############################### "+res.status)
                tokenProvider.setAccessToken(res.data.accessToken)
                tokenProvider.setRefreshToken(res.data.refreshToken)
                res.data.accessToken
            } catch (e: Exception) {
                Timber.e("===== refreshToken failed =====")
                Timber.e("Exception type: ${e.javaClass.simpleName}")
                Timber.e("Exception message: ${e.message}")

                // Retrofit/OkHttp 에러인 경우 상세 정보
                when (e) {
                    is retrofit2.HttpException -> {
                        Timber.e("HTTP Status Code: ${e.code()}")
                        Timber.e("HTTP Message: ${e.message()}")

                        // 응답 본문 읽기
                        try {
                            val errorBody = e.response()?.errorBody()?.string()
                            Timber.e("Error Response Body: $errorBody")
                        } catch (bodyException: Exception) {
                            Timber.e("Failed to read error body: ${bodyException.message}")
                        }
                    }
                    is java.io.IOException -> {
                        Timber.e("Network error (IOException): ${e.message}")
                    }
                    is kotlinx.serialization.SerializationException -> {
                        Timber.e("Serialization error: ${e.message}")
                    }
                    else -> {
                        Timber.e("Unknown error type")
                    }
                }

                // 전체 스택 트레이스
                Timber.e(e, "Full stack trace:")

                runBlocking {
                    userLocalDataSource.clear()
                }
                authEventBus.tryEmit(AuthEvent.Expired)

                null
            }
        }
    }

    private suspend inline fun <T> Mutex.lockAndGet(block: () -> T): T {
        lock()
        return try { block() } finally { unlock() }
    }
}