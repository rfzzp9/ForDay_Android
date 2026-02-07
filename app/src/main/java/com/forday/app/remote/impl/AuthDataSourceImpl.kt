package com.forday.app.remote.impl

import com.forday.app.data.model.CancelAccountEntity
import com.forday.app.data.model.KakaoLoginEntity
import com.forday.app.data.model.LogoutEntity
import com.forday.app.data.model.SwitchAccountEntity
import com.forday.app.data.remote.AuthDataSource
import com.forday.app.remote.api.service.AuthApi
import com.forday.app.remote.api.service.UserApi
import com.forday.app.remote.model.request.GuestLoginRequest
import com.forday.app.remote.model.request.KakaoLoginRequest
import com.forday.app.remote.model.request.SwitchAccountRequest
import com.forday.app.remote.model.response.GuestLoginResponse
import com.forday.app.remote.model.response.KakaoLoginResponse
import com.forday.app.remote.model.response.LogoutResponse
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class AuthDataSourceImpl @Inject constructor(
    private val authApi: AuthApi,
    private val userApi: UserApi
) : AuthDataSource {

    override suspend fun kakaoLogin(kakaoAccessToken: KakaoLoginRequest): KakaoLoginEntity {
     //   return authApi.kakaoLogin(kakaoAccessToken).toData()
        return try {
            val response = authApi.kakaoLogin(kakaoAccessToken).toData()
            Timber.d("카카오 로그인 성공: $response")
            response
        } catch (e: HttpException) {
            // 4xx, 5xx 서버 응답 에러
            Timber.e(e, "카카오 API 서버 오류 발생 (Status: ${e.code()})")
            Timber.e("에러 내용: ${e.response()?.errorBody()?.string()}")
            // 필요 시 응답 바디 로그: Timber.e("에러 내용: ${e.response()?.errorBody()?.string()}")
            throw e
        } catch (e: IOException) {
            // 네트워크 연결 끊김, 타임아웃 등
            Timber.e(e, "네트워크 연결 오류 또는 타임아웃 발생")
            throw e
        } catch (e: Exception) {
            // 그 외 데이터 파싱 오류 등 예상치 못한 에러
            Timber.e(e, "카카오 로그인 중 알 수 없는 예외 발생")
            throw e
        }
    }

    override suspend fun guestLogin(guestUserId: GuestLoginRequest): GuestLoginResponse =
        authApi.guestLogin(guestUserId)

    override suspend fun switchAccount(switchAccountRequest: SwitchAccountRequest): SwitchAccountEntity =
        userApi.switchAccount(switchAccountRequest).toData()

    override suspend fun logout(): LogoutEntity =
        userApi.logout().toData()

    override suspend fun cancelAccount(): CancelAccountEntity =
        userApi.cancelAccount().toData()

}