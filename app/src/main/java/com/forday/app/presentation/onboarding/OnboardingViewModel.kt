package com.forday.app.presentation.onboarding

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavKey
import com.forday.app.core.logger.analytics.AnalyticsEvent
import com.forday.app.core.logger.analytics.AnalyticsManager
import com.forday.app.core.firebase.ONBOARDING_VARIANT_KEY
import com.forday.app.core.util.UserMessageCategory
import com.forday.app.core.util.toUserMessage
import com.forday.app.domain.usecase.ConsentTermsUseCase
import com.forday.app.domain.usecase.GetAccessTokenUseCase
import com.forday.app.domain.usecase.GetGuestUserIdUseCase
import com.forday.app.domain.usecase.GetHasSeenIntroUseCase
import com.forday.app.domain.usecase.GetIsNicknameSetUseCase
import com.forday.app.domain.usecase.GetIsOnboardingCompletedUseCase
import com.forday.app.domain.usecase.GetSocialTypeUseCase
import com.forday.app.domain.usecase.GuestLoginUseCase
import com.forday.app.domain.usecase.KakaoLoginUseCase
import com.forday.app.domain.usecase.RemoveOnboardingDataUseCase
import com.forday.app.domain.usecase.SaveHasSeenIntroUseCase
import com.forday.app.presentation.BaseViewModel
import com.forday.app.presentation.common.SnackbarManager
import com.forday.app.presentation.home.navigation.Home
import com.forday.app.presentation.onboarding.hobbyselect.navigation.SelectHobby
import com.forday.app.presentation.onboarding.experiment.OnboardingAbVariant
import com.forday.app.presentation.onboarding.login.navigation.Login
import com.forday.app.presentation.onboarding.periodselect.navigation.SelectPeriod
import com.forday.app.presentation.onboarding.timeselect.ScreenMode
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import com.forday.app.presentation.onboarding.swipeintro.navigation.SwipeIntro as SwipeIntroRoute

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val analyticsManager: AnalyticsManager,
    private val kakaoLoginUseCase: KakaoLoginUseCase,
    private val guestLoginUseCase: GuestLoginUseCase,
    private val getAccessTokenUseCase: GetAccessTokenUseCase,
    private val getIsOnboardingCompletedUseCase: GetIsOnboardingCompletedUseCase,
    private val getIsNicknameSetUseCase: GetIsNicknameSetUseCase,
    private val removeOnboardingDataUseCase: RemoveOnboardingDataUseCase,
    private val getHasSeenIntroUseCase: GetHasSeenIntroUseCase,
    private val saveHasSeenIntroUseCase: SaveHasSeenIntroUseCase,
    private val getSocialTypeUseCase: GetSocialTypeUseCase,
    private val getGuestUserIdUseCase: GetGuestUserIdUseCase,
    private val snackbarManager: SnackbarManager,
    private val consentTermsUseCase: ConsentTermsUseCase,
    private val firebaseRemoteConfig: FirebaseRemoteConfig,
) : BaseViewModel<OnboardingSideEffect>() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.toStateIn()

    init {
        fetchOnboardingAbVariant()
        viewModelScope.launch {
            autoGuestLoginIfNeeded()
            getUserData()
            determineInitialRoute()
        }
        viewModelScope.launch {
            getHasSeenIntroUseCase()
                .catch { }
                .collect { hasSeenIntro ->
                    _uiState.update { it.copy(hasSeenIntro = hasSeenIntro) }
                }
        }
        viewModelScope.launch {
            getSocialTypeUseCase()
                .catch { }
                .collect { socialType ->
                    _uiState.update { it.copy(socialType = socialType) }
                }
        }
    }

    fun onAction(action: OnboardingAction) = viewModelScope.launch {
        when (action) {
            is OnboardingAction.OnClose -> Unit
        }
    }

    private fun fetchOnboardingAbVariant() {
        firebaseRemoteConfig.fetchAndActivate()
            .addOnCompleteListener {
                val variant = OnboardingAbVariant.fromRemoteValue(
                    firebaseRemoteConfig.getString(ONBOARDING_VARIANT_KEY)
                )
                analyticsManager.setUserProperty(ONBOARDING_VARIANT_KEY, variant.remoteValue)
                _uiState.update { it.copy(onboardingAbVariant = variant) }
            }
    }

    private fun determineInitialRoute() =
        uiState
            .filter { it.accessToken != null || it.isOnboardingCompleted != null }
            .take(1)
            .onEach {
                if (_uiState.value.isSplashLoading) {
                    delay(2000)
                }

                val freshState = _uiState.value
                val route = when {
                    freshState.accessToken == null && freshState.hasSeenIntro == false -> SwipeIntroRoute
                    freshState.accessToken == null -> Login
                    freshState.isOnboardingCompleted == false -> SelectHobby
                    freshState.isOnboardingCompleted == true && freshState.isNicknameSet == true -> Home
                    else -> SelectPeriod(mode = ScreenMode.ONBOARDING)
                }

                _uiState.update {
                    it.copy(
                        initialRoute = route as NavKey?,
                        isSplashLoading = false,
                    )
                }
            }
            .catch { }
            .launchIn(viewModelScope)

    private fun getUserData() = viewModelScope.launch {
        combine(
            getAccessTokenUseCase(),
            getIsOnboardingCompletedUseCase(),
            getIsNicknameSetUseCase(),
        ) { accessToken, isOnboardingCompleted, isNicknameSet ->
            Triple(accessToken, isOnboardingCompleted, isNicknameSet)
        }.catch { throwable ->
            snackbarManager.show(throwable.toUserMessage(UserMessageCategory.AUTH))
        }.collect { (accessToken, isOnboardingCompleted, isNicknameSet) ->
            _uiState.update {
                it.copy(
                    accessToken = accessToken,
                    isOnboardingCompleted = isOnboardingCompleted,
                    isNicknameSet = isNicknameSet,
                )
            }
        }
    }

    fun consentTerms(
        serviceConsent: Boolean,
        ageOver14Consent: Boolean,
        privateConsent: Boolean,
        recordPushConsent: Boolean,
    ) = viewModelScope.launch {
        val isGuest = _uiState.value.socialType == "GUEST"
        val needsPermissionRequest = !isGuest &&
            recordPushConsent &&
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

        if (needsPermissionRequest) {
            _uiState.update {
                it.copy(
                    pendingServiceConsent = serviceConsent,
                    pendingAgeOver14Consent = ageOver14Consent,
                    pendingPrivateConsent = privateConsent,
                )
            }
            _sideEffectChannel.send(OnboardingSideEffect.RequestNotificationPermission)
        } else {
            submitConsentToServer(serviceConsent, ageOver14Consent, privateConsent, recordPushConsent)
        }
    }

    fun submitConsentWithPermissionResult(isGranted: Boolean) = viewModelScope.launch {
        val state = _uiState.value
        val serviceConsent = state.pendingServiceConsent ?: return@launch
        val ageOver14Consent = state.pendingAgeOver14Consent ?: return@launch
        val privateConsent = state.pendingPrivateConsent ?: return@launch
        submitConsentToServer(serviceConsent, ageOver14Consent, privateConsent, isGranted)
    }

    private suspend fun submitConsentToServer(
        serviceConsent: Boolean,
        ageOver14Consent: Boolean,
        privateConsent: Boolean,
        recordPushConsent: Boolean,
    ) {
        flow {
            emit(consentTermsUseCase(serviceConsent, ageOver14Consent, privateConsent, recordPushConsent))
        }.catch { throwable ->
            snackbarManager.show(throwable.toUserMessage())
        }.collect {
            _sideEffectChannel.send(OnboardingSideEffect.TermsConsentSuccess(recordPushConsent))
        }
    }

    private suspend fun autoGuestLoginIfNeeded() {
        runCatching {
            val accessToken = getAccessTokenUseCase().first()
            val socialType = getSocialTypeUseCase().first()
            val guestUserId = getGuestUserIdUseCase().first()
            if (accessToken == null && socialType == "GUEST" && guestUserId != null) {
                guestLoginUseCase()
            }
        }.onFailure { throwable ->
            Timber.e("autoGuestLoginIfNeeded error: $throwable")
        }
    }

    fun guestAutoReLogin() = viewModelScope.launch {
        guestLoginUseCase()
            .onSuccess {
                _sideEffectChannel.send(OnboardingSideEffect.GuestAutoReLoginSuccess)
            }
            .onFailure {
                _sideEffectChannel.send(OnboardingSideEffect.GuestAutoReLoginFailure)
            }
    }

    fun resetForNewSession() = viewModelScope.launch {
        runCatching {
            removeOnboardingDataUseCase()
        }.onFailure { throwable ->
            Timber.e("removeOnboardingData error: $throwable")
        }

        _uiState.update {
            it.copy(
                isLoginSuccess = false,
                isNewUser = null,
                isNicknameSet = null,
                isOnboardingCompleted = null,
                errorData = null,
                error = "",
            )
        }
    }

    fun loginWithGuest() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true, error = "") }
        try {
            guestLoginUseCase()
                .onSuccess { data ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isNewUser = data.isNewUser,
                            isOnboardingCompleted = data.onboardingCompleted,
                            isNicknameSet = data.nicknameSet,
                            isLoginSuccess = true,
                        )
                    }
                }
                .onFailure { error ->
                    val errorMessage = error.toUserMessage(UserMessageCategory.AUTH)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isLoginSuccess = false,
                            error = errorMessage,
                        )
                    }
                    snackbarManager.show(errorMessage)
                }
        } catch (e: Exception) {
            val errorMessage = e.toUserMessage(UserMessageCategory.AUTH)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    isLoginSuccess = false,
                    error = errorMessage,
                )
            }
            snackbarManager.show(errorMessage)
        }
    }

    fun loginWithKakao(context: Context) {
        val kakao = UserApiClient.instance

        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null || token == null) {
                val errorMessage = error?.toUserMessage(UserMessageCategory.AUTH)
                    ?: "로그인에 실패했어요. 다시 시도해 주세요."
                snackbarManager.show(errorMessage)
            } else {
                loginIntoApp(token.accessToken)
            }
        }

        if (kakao.isKakaoTalkLoginAvailable(context)) {
            kakao.loginWithKakaoTalk(context) { token, error ->
                if (error != null || token == null) {
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        snackbarManager.show(error.toUserMessage(UserMessageCategory.AUTH))
                        return@loginWithKakaoTalk
                    }
                    UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
                } else {
                    loginIntoApp(token.accessToken)
                }
            }
        } else {
            kakao.loginWithKakaoAccount(context, callback = callback)
        }
    }

    fun logEvent(logEvent: String) {
        analyticsManager.logEvent(logEvent)
    }

    fun logEvent(event: AnalyticsEvent) {
        analyticsManager.logEvent(event)
    }

    fun saveHasSeenIntro() = viewModelScope.launch {
        runCatching {
            saveHasSeenIntroUseCase(true)
        }
    }

    private fun loginIntoApp(kakaoAccessToken: String) = viewModelScope.launch {
        try {
            kakaoLoginUseCase(kakaoAccessToken)
                .onSuccess { data ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isNewUser = data.data.isNewUser,
                            isOnboardingCompleted = data.data.isOnboardingCompleted,
                            isNicknameSet = data.data.isNicknameSet,
                            isLoginSuccess = true,
                        )
                    }
                }
                .onFailure { error ->
                    val errorMessage = error.toUserMessage(UserMessageCategory.AUTH)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isLoginSuccess = false,
                        )
                    }
                    snackbarManager.show(errorMessage)
                }
        } catch (e: Exception) {
            val errorMessage = e.toUserMessage(UserMessageCategory.AUTH)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    error = errorMessage,
                    isLoginSuccess = false,
                )
            }
            snackbarManager.show(errorMessage)
        }
    }

    @Suppress("unused")
    private suspend fun logoutFromKakao(): Boolean =
        suspendCoroutine { continuation ->
            UserApiClient.instance.logout { error ->
                if (error != null) {
                    Log.e("TAG", "logout failed", error)
                    continuation.resume(false)
                } else {
                    continuation.resume(true)
                }
            }
        }
}
