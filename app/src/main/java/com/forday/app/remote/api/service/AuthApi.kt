package com.forday.app.remote.api.service

import com.forday.app.remote.model.request.GuestLoginRequest
import com.forday.app.remote.model.request.KakaoLoginRequest
import com.forday.app.remote.model.request.RefreshRequest
import com.forday.app.remote.model.request.SwitchAccountRequest
import com.forday.app.remote.model.response.AccessTokenResponse
import com.forday.app.remote.model.response.GuestLoginResponse
import com.forday.app.remote.model.response.KakaoLoginResponse
import com.forday.app.remote.model.response.SwitchAccountResponse
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.POST

interface AuthApi {
    @POST("/auth/kakao")
    suspend fun kakaoLogin(
        @Body params: KakaoLoginRequest
    ): KakaoLoginResponse

    @POST("/auth/guest")
    suspend fun guestLogin(
        @Body request: GuestLoginRequest
    ): GuestLoginResponse
}