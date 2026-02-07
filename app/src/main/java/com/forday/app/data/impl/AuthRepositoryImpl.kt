package com.forday.app.data.impl

import com.forday.app.core.datastore.UserLocalDataSource
import com.forday.app.data.model.toDomain
import com.forday.app.data.remote.AuthDataSource
import com.forday.app.domain.model.CancelAccountDomain
import com.forday.app.domain.model.KakaoLoginDomain
import com.forday.app.domain.model.LogoutDomain
import com.forday.app.domain.model.SwitchAccountDomain
import com.forday.app.domain.repository.AuthRepository
import com.forday.app.remote.model.request.GuestLoginRequest
import com.forday.app.remote.model.request.KakaoLoginRequest
import com.forday.app.remote.model.request.SwitchAccountRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import timber.log.Timber
import javax.inject.Inject
import kotlin.runCatching

internal class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val userLocalDataSource: UserLocalDataSource
) : AuthRepository {
    override suspend fun kakaoLogin(kakaoAccessToken: String) : Result<KakaoLoginDomain> =
        runCatching {
            val loginData = authDataSource.kakaoLogin(KakaoLoginRequest(kakaoAccessToken)).toDomain()
            Timber.d("@@@@@@@loginResponse: ${loginData.data.accessToken}")
            // 로컬에 토큰 저장
            userLocalDataSource.saveKakaoToken(
                loginData.data.accessToken,
                loginData.data.refreshToken,
                loginData.data.socialType
            )
            Timber.d("@@@@@@@토큰 저장 완료: ${loginData.data.accessToken}"+" ${loginData.data.refreshToken}")
            // login data 반환
            loginData
        }

    override suspend fun kakaoLogout(): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun guestLogin(): Result<Boolean> =
        runCatching {
            val userId = userLocalDataSource.guestIdFlow.first()
            val loginResponse = authDataSource.guestLogin(GuestLoginRequest(userId))
            Timber.d("@@@@@@@loginResponse: ${loginResponse.data.accessToken}  and  ${loginResponse.data.refreshToken}  and  ${loginResponse.data.userId}  and  ${loginResponse.data.socialType}  and  ${loginResponse.data.isNewUser}")
            // 로컬에 토큰, userId 저장
            userLocalDataSource.saveGuestTokenAndId(
                loginResponse.data.accessToken,
                loginResponse.data.refreshToken,
                loginResponse.data.userId,
                loginResponse.data.socialType
            )
            // 신규 사용자 여부 반환 -> 온보딩 스킵 위함
            loginResponse.data.isNewUser
        }

    override fun getAccessToken(): Flow<String?> =
        userLocalDataSource.getAccessToken()

    override fun getIsOnboardingCompleted(): Flow<Boolean> =
        userLocalDataSource.getIsOnboardingCompleted()

    override fun getIsNicknameSet(): Flow<Boolean?> =
        userLocalDataSource.getIsNicknameSet()


    override suspend fun saveIsOnboardingCompleted(isOnboardingCompleted: Boolean) =
        userLocalDataSource.saveIsOnboardingCompleted(isOnboardingCompleted)


    override suspend fun saveIsNicknameSet(isNicknameSet: Boolean) =
        userLocalDataSource.saveIsNicknameSet(isNicknameSet)


    override suspend fun removeAccessToken() {
        userLocalDataSource.removeAccessToken()
    }

    override suspend fun switchAccount(
        socialType: String,
        kakaoAccessToken: String
    ): Result<SwitchAccountDomain> = runCatching {
        val loginData = authDataSource.switchAccount(SwitchAccountRequest(socialType, kakaoAccessToken)).toDomain()
        Timber.d("@@@@@@@switchAccount loginResponse: ${loginData.accessToken}")
        userLocalDataSource.saveKakaoToken(
            loginData.accessToken,
            loginData.refreshToken,
            loginData.socialType
        )
        Timber.d("@@@@@@@switchAccount 토큰 저장 완료: ${loginData.accessToken}"+" ${loginData.refreshToken}")
        loginData
    }

    override suspend fun logout() = runCatching {
        val logoutData = authDataSource.logout().toDomain()
        userLocalDataSource.removeTokenAndLoginType()
        userLocalDataSource.removeUserInfo()
        logoutData
    }

    override suspend fun cancelAccount() = runCatching {
        val cancelAccountData = authDataSource.cancelAccount().toDomain()
        userLocalDataSource.removeTokenAndLoginType()
        userLocalDataSource.removeUserInfo()
        cancelAccountData
    }

}