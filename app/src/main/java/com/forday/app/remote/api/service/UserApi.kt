package com.forday.app.remote.api.service

import com.forday.app.remote.model.request.ProfileImageRequest
import com.forday.app.remote.model.request.RegisterNicknameRequest
import com.forday.app.remote.model.request.SwitchAccountRequest
import com.forday.app.remote.model.response.IsNicknameDuplicateResponse
import com.forday.app.remote.model.response.ProfileImageResponse
import com.forday.app.remote.model.response.ProfileResponse
import com.forday.app.remote.model.response.RegisterNicknameResponse
import com.forday.app.remote.model.response.SwitchAccountResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Query

interface UserApi {

    @GET("/users/nickname/availability")
    suspend fun checkNicknameDuplicate(
        @Query("nickname") nickname: String
    ): IsNicknameDuplicateResponse

    @PATCH("/users/nickname")
    suspend fun registerNickname(
        @Body nickname: RegisterNicknameRequest
    ): RegisterNicknameResponse

    @GET("/users/info")
    suspend fun getUserInfo(): ProfileResponse  // 사용자 정보 조회

    @PATCH("/users/profile-image")
    suspend fun setProfileImage(    // 사용자 프로필 이미지 설정
        @Body body: ProfileImageRequest
    ): ProfileImageResponse

    @PATCH("/auth/switch-account")
    suspend fun switchAccount(
        @Body request: SwitchAccountRequest
    ): SwitchAccountResponse
}