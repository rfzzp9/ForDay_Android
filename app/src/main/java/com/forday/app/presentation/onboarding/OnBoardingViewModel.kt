package com.forday.app.presentation.onboarding

import android.content.Context
import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavKey
import com.forday.app.core.logger.analytics.AnalyticsManager
import com.forday.app.domain.usecase.CreateHobbyUseCase
import com.forday.app.domain.usecase.GetAccessTokenUseCase
import com.forday.app.domain.usecase.GetHobbyDataUseCase
import com.forday.app.domain.usecase.GetIsNicknameDuplicateUseCase
import com.forday.app.domain.usecase.GetIsNicknameSetUseCase
import com.forday.app.domain.usecase.GetIsOnboardingCompletedUseCase
import com.forday.app.domain.usecase.GetOnboardingDataUseCase
import com.forday.app.domain.usecase.GuestLoginUseCase
import com.forday.app.domain.usecase.KakaoLoginUseCase
import com.forday.app.domain.usecase.ModifyHobbyDurationUseCase
import com.forday.app.domain.usecase.ModifyHobbyExecutionCountUseCase
import com.forday.app.domain.usecase.ModifyHobbyTimeUseCase
import com.forday.app.domain.usecase.RegisterNicknameUseCase
import com.forday.app.domain.usecase.SaveIsNicknameSetUseCase
import com.forday.app.domain.usecase.SaveIsOnboardingCompletedUseCase
import com.forday.app.domain.usecase.SaveNicknameUseCase
import com.forday.app.domain.usecase.SaveOnboardingDataUseCase
import com.forday.app.presentation.BaseViewModel
import com.forday.app.presentation.home.navigation.Home
import com.forday.app.presentation.modifyhobby.ModifyHobbySideEffect
import com.forday.app.presentation.onboarding.hobbyselect.navigation.SelectHobby
import com.forday.app.presentation.onboarding.login.navigation.Login
import com.forday.app.presentation.onboarding.periodselect.JourneyMode
import com.forday.app.presentation.onboarding.periodselect.navigation.SelectPeriod
import com.forday.app.presentation.onboarding.timeselect.ScreenMode
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
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

import com.forday.app.core.util.UserMessageCategory
import com.forday.app.core.util.toUserMessage

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val analyticsManager: AnalyticsManager,
    private val getHobbyDataUseCase: GetHobbyDataUseCase,
    private val kakaoLoginUseCase: KakaoLoginUseCase,
    private val guestLoginUseCase: GuestLoginUseCase,
    private val getAccessTokenUseCase: GetAccessTokenUseCase,
    private val getIsOnboardingCompletedUseCase: GetIsOnboardingCompletedUseCase,
    private val getIsNicknameSetUseCase: GetIsNicknameSetUseCase,
    private val getOnboardingDataUseCase: GetOnboardingDataUseCase,
    private val getIsNicknameDuplicateUseCase: GetIsNicknameDuplicateUseCase,
    private val registerNicknameUseCase: RegisterNicknameUseCase,
    private val saveNicknameUseCase: SaveNicknameUseCase,
    private val saveOnboardingDataUseCase: SaveOnboardingDataUseCase,
    private val createHobbyUseCase: CreateHobbyUseCase,
    private val saveIsOnboardingCompletedUseCase: SaveIsOnboardingCompletedUseCase,
    private val saveIsNicknameSetUseCase: SaveIsNicknameSetUseCase,
    private val modifyHobbyTimeUseCase: ModifyHobbyTimeUseCase, // 취미 정보 수정 - 취미 시간
    private val modifyHobbyExecutionCountUseCase: ModifyHobbyExecutionCountUseCase, // 취미 정보 수정 - 취미 주당 횟수
    private val modifyHobbyDurationUseCase: ModifyHobbyDurationUseCase
) : BaseViewModel<OnboardingSideEffect>() {

    private val _uiState: MutableStateFlow<OnboardingUiState> = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.toStateIn()

    private val _shouldAutoAdvanceFromTime = MutableStateFlow(true)
    val shouldAutoAdvanceFromTime: StateFlow<Boolean> = _shouldAutoAdvanceFromTime.asStateFlow()

    private val _shouldAutoAdvanceFromPurpose = MutableStateFlow(true)
    val shouldAutoAdvanceFromPurpose: StateFlow<Boolean> = _shouldAutoAdvanceFromPurpose.asStateFlow()

    private val _shouldAutoAdvanceFromFrequency = MutableStateFlow(true)
    val shouldAutoAdvanceFromFrequency: StateFlow<Boolean> = _shouldAutoAdvanceFromFrequency.asStateFlow()

    init {
        Timber.e("@@@@@@@@@@@@@ 호출1 "+uiState.value.isOnboardingCompleted+", "+uiState.value.isNicknameSet+", "+uiState.value.accessToken)
        viewModelScope.launch {
            getUserData()
            determineInitialRoute()
        }
    }

    fun onAction(action: OnboardingAction) = viewModelScope.launch {  //아직 사용중이지 않음
        when (action) {
            is OnboardingAction.OnClose -> _sideEffectChannel.send(OnboardingSideEffect.OnClose)
        }
    }

    fun fetchHobbyData() = viewModelScope.launch {
        flow {
            emit(getHobbyDataUseCase().toPresentation())
        }.catch { throwable ->
            Timber.e("@#@@@@@@@@@@@@111 "+throwable)
            _sideEffectChannel.send(OnboardingSideEffect.Exception(throwable))
        }.collect { data ->
            Timber.e("@#@@@@@@@@@@@@222 "+data.hobbies.size)
            _uiState.update {
                it.copy(
                    hobbies = data.hobbies,
                    appVersion = data.appVersion
                )
            }
        }
    }

    fun updateCustomHobbyText(text: String) {
        _uiState.update { it.copy(customHobbyText = text) }
    }

    // 커스텀 다이얼로그 표시
    fun showDialog() {
        _uiState.update { it.copy(showDialog = true) }
    }

    // 커스텀 다이얼로그 닫기 (취소)
    fun dismissDialog() {
        _uiState.update {
            it.copy(showDialog = false)
            // customHobbyText는 유지 (초기화하지 않음)
        }
    }

    // HobbyCard 선택 시
    fun saveHobbyInfo(hobbyId: Long?, hobbyName: String) {
        _uiState.update {
            it.copy(
                selectedHobbyId = hobbyId,
                customHobbyText = "",
                selectedHobbyName = hobbyName
            )
        }
    }

    // 커스텀 취미 확인 시
    fun confirmCustomHobby(text: String) {
        _uiState.update {
            it.copy(
                customHobbyText = text,
                showDialog = false,
                selectedHobbyId = 0,
                selectedHobbyName = text
            )
        }
    }

    fun savePurposes(purposes: Set<String>) {
        _uiState.update {
            it.copy(
                selectedPurpose = purposes.joinToString(","),  // Set을 콤마로 구분된 문자열로 변환
                customPurposeText = "" // 목적 카드 선택 시 커스텀 텍스트 초기화
            )
        }
    }

    // 커스텀 목적 확인 (기존 메서드)
    fun confirmCustomPurpose(text: String) {
        _uiState.update {
            it.copy(
                customPurposeText = text,
                selectedPurpose = "" // 커스텀 목적 입력 시 선택된 목적 초기화
            )
        }
    }

    // OnBoardingViewModel.kt에 추가/수정이 필요한 부분

    // 목적 선택 메서드 (새로 추가)
    fun selectPurpose(purpose: String) {
        _uiState.update {
            it.copy(
                selectedPurpose = purpose,
                customPurposeText = "" // 목적 카드 선택 시 커스텀 텍스트 초기화
            )
        }
    }

    //시간 선택
    fun saveTime(minutes: Int) {
        Timber.d("ViewModel selectTime called: $minutes")
        _uiState.update {
            it.copy(selectedMinutes = minutes)
        }
    }

    fun disableAutoAdvanceFromTime() {
        _shouldAutoAdvanceFromTime.value = false
    }

    fun enableAutoAdvanceFromTime() {
        _shouldAutoAdvanceFromTime.value = true
    }

    fun disableAutoAdvanceFromPurpose() {
        _shouldAutoAdvanceFromPurpose.value = false
    }

    fun enableAutoAdvanceFromPurpose() {
        _shouldAutoAdvanceFromPurpose.value = true
    }

    fun disableAutoAdvanceFromFrequency() {
        _shouldAutoAdvanceFromFrequency.value = false
    }

    fun enableAutoAdvanceFromFrequency() {
        _shouldAutoAdvanceFromFrequency.value = true
    }

    fun saveFrequency(frequency: Int) {
        _uiState.update {
            it.copy(selectedFrequency = frequency)
        }
    }

    private fun determineInitialRoute() =
        uiState
            .filter { it.accessToken != null || it.isOnboardingCompleted != null }
            .take(1)
            .onEach { state ->
                if (state.isSplashLoading) {
                    delay(2000)  // Splash 2초 todo 조건 걸기
                }
                val route = when {
                    state.accessToken == null -> Login
                    state.isOnboardingCompleted == false -> SelectHobby
                    state.isOnboardingCompleted == true && state.isNicknameSet == true -> Home
                    else -> {
                        getOnboardingData()
                        SelectPeriod(mode = ScreenMode.ONBOARDING)
                    }
                }

                _uiState.update {
                    it.copy(initialRoute = route as NavKey?, isSplashLoading = false)
                }
            }
            .catch {

            }
            .launchIn(viewModelScope)


    fun selectJourneyMode(mode: JourneyMode) {
        _uiState.update { it.copy(selectedJourneyMode = mode) }
    }

    private fun getUserData() = viewModelScope.launch {

        combine(
            getAccessTokenUseCase(),
            getIsOnboardingCompletedUseCase(),
            getIsNicknameSetUseCase(),
        ) { accessToken, isOnboardingCompleted, isNicknameSet -> Triple(accessToken, isOnboardingCompleted, isNicknameSet) }
            .catch { throwable ->
                _sideEffectChannel.send(OnboardingSideEffect.Exception(throwable))
            }.collect { (accessToken, isOnboardingCompleted, isNicknameSet) ->
                Timber.e("@@@@@@@@isNicknameSet@@@"+isNicknameSet+", "+isOnboardingCompleted)
                _uiState.update {
                    it.copy(
                        accessToken = accessToken,
                        isOnboardingCompleted = isOnboardingCompleted,
                        isNicknameSet = isNicknameSet
                    )
                }
            }
    }

    fun getOnboardingData() = viewModelScope.launch {
        getOnboardingDataUseCase()
            .catch { throwable ->
                _sideEffectChannel.send(OnboardingSideEffect.Exception(throwable))
            }
            .collect { onboardingData ->
                _uiState.update {
                    it.copy(
                        selectedHobbyId = onboardingData.hobbyId?.toLong(),
                        selectedHobbyName = onboardingData.hobbyName,
                        selectedMinutes = onboardingData.hobbyTimeMinutes,
                        selectedPurpose = onboardingData.hobbyPurpose,
                        selectedFrequency = onboardingData.executionCount,
                        selectedJourneyMode = if (onboardingData.durationSet == true) JourneyMode.FORDAY_66 else JourneyMode.FREE
                    )
                }
            }
    }

    fun getIsNicknameDuplicate(nickName: String) = viewModelScope.launch {
        flow {
            emit(getIsNicknameDuplicateUseCase(nickName))
        }.catch { throwable ->
            Timber.e("@@@@@@@@@@@@@@@@@@@@@@@@@@@ "+throwable)
            _sideEffectChannel.send(OnboardingSideEffect.Exception(throwable))
        }.collect { result ->
            Timber.e("@@@@@@@@@@@@@@@@@@@@@@@@@@@ "+result.data.nickname+", "+result.data.available+", "+result.data.message)
            _uiState.update {
                it.copy(
                    selectedHobbyName = result.data.nickname,
                    nicknameCheckMessage = result.data.message,
                    isNicknameChecked = result.data.available
                )
            }
        }
    }

    fun resetNicknameCheck() {
        _uiState.update {
            it.copy(
                nicknameCheckMessage = "",
                isNicknameChecked = false
            )
        }
    }

    fun registerNickname(nickName: String?) = viewModelScope.launch {
        flow {
            emit(registerNicknameUseCase(nickName))
        }.catch { throwable ->
            _sideEffectChannel.send(OnboardingSideEffect.Exception(throwable))
        }.collect { data ->
            _uiState.update {
                it.copy(
                    nicknameRegisterSuccess = data.isSuccess
                )
            }
            nickName?.let { saveNicknameUseCase(it) }
        }
    }

    fun saveOnboardingData(  //온보딩 데이터 로컬 저장..?하는 메서드
        selectedHobbyId: Long?,
        selectedHobbyName: String?,
        selectedMinutes: Int?,
        selectedPurpose: String?,
        selectedFrequency: Int?,
        selectedPeriod: JourneyMode?
    ) = viewModelScope.launch {

        runCatching {
            saveOnboardingDataUseCase(
                selectedHobbyId,
                selectedHobbyName,
                selectedMinutes,
                selectedPurpose,
                selectedFrequency,
                selectedPeriod == JourneyMode.FORDAY_66
            )
        }.onFailure { throwable ->
            _sideEffectChannel.send(OnboardingSideEffect.Exception(throwable))
        }
    }

    fun createHobby(  // 여정일 쪽에서 저장
        selectedHobbyId: Long? = null,
        selectedHobbyName: String?,
        selectedMinutes: Int?,
        selectedPurpose: String?,
        selectedFrequency: Int?,
        selectedPeriod: JourneyMode?
    ) = viewModelScope.launch {
        Timber.e("@@@@@@@@@@@@@ 호출 "+selectedHobbyId+", "+selectedHobbyName+", "+selectedMinutes+", "+selectedPurpose+", "+selectedFrequency+", "+(selectedPeriod == JourneyMode.FORDAY_66))
        flow {
            emit(
                createHobbyUseCase(
                    selectedHobbyId,
                    selectedHobbyName,
                    selectedMinutes,
                    selectedPurpose,
                    selectedFrequency,
                    selectedPeriod == JourneyMode.FORDAY_66
                )
            )
        }.catch { throwable ->
            Timber.e("@@@@@@@@@@@@@ "+throwable.stackTrace+", "+throwable.cause+", "+throwable)
            _sideEffectChannel.send(OnboardingSideEffect.Exception(throwable))
        }.collect { result ->
            Timber.e("@@@@@@@@@@@@@ "+result.data.message)
            _uiState.update {
                it.copy(
                    isOnboardingDataSaved = result.isSuccess,
                    hobbyId = result.data.hobbyId
                )
            }
        }
    }

    fun loginWithGuest() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true, error = "") }
        try {
            guestLoginUseCase()
                .onSuccess { isNewUser ->
                    Timber.d("isNewUser "+isNewUser)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isNewUser = isNewUser,
                            isLoginSuccess = true
                        )
                    }
                }
                .onFailure { error ->
                    Timber.d("error "+error.message)
                    val errorMessage = error.toUserMessage(UserMessageCategory.AUTH)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isLoginSuccess = false,  // 로그인 실패
                            error = errorMessage
                        )
                    }
                    sendSideEffect(OnboardingSideEffect.DomainError(errorMessage))
                }
        } catch (e: Exception) {
            val errorMessage = e.toUserMessage(UserMessageCategory.AUTH)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    isLoginSuccess = false,  // 로그인 실패
                    error = errorMessage
                )
            }
            sendSideEffect(OnboardingSideEffect.DomainError(errorMessage))
        }
    }


    fun loginWithKakao(context: Context) {
        val kakao = UserApiClient.instance

        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null || token == null) {
                val errorMessage = error?.toUserMessage(UserMessageCategory.AUTH)
                    ?: "로그인에 실패했어요. 잠시 후 다시 시도해주세요."
                sendSideEffect(OnboardingSideEffect.DomainError(errorMessage))
            } else {
                loginIntoApp(token.accessToken)
                Timber.d("token.accessToken ${token.accessToken}")
            }
        }

        if (kakao.isKakaoTalkLoginAvailable(context)) {
            kakao.loginWithKakaoTalk(context) { token, error ->
                if (error != null || token == null) {
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        sendSideEffect(OnboardingSideEffect.DomainError(error.toUserMessage(UserMessageCategory.AUTH)))
                        return@loginWithKakaoTalk
                    }
                    UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
                } else {
                    loginIntoApp(token.accessToken)
                    Timber.d("token.accessToken ${token.accessToken}")
                }
            }
        } else {
            kakao.loginWithKakaoAccount(context, callback = callback)
        }
    }

    fun sendSideEffect(sideEffect: OnboardingSideEffect) = viewModelScope.launch {
        _sideEffectChannel.send(sideEffect)
    }

    private fun loginIntoApp(kakaoAccessToken: String) = viewModelScope.launch {
        try {
            kakaoLoginUseCase(kakaoAccessToken)
                .onSuccess { data ->
                    Timber.e("@@@@@@@@@@@@@@@@@data "+data)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isNewUser = data.data.isNewUser,
                            isLoginSuccess = true
                        )
                    }
                }
                .onFailure { error ->
                    Timber.e("@@@@@@@@@@@@@@@@@ error "+error)
                    val errorMessage = error.toUserMessage(UserMessageCategory.AUTH)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isLoginSuccess = false
                        )
                    }
                    sendSideEffect(OnboardingSideEffect.DomainError(errorMessage))
                }
        } catch (e: Exception) { // loginUseCase 호출 자체에서 발생한 예외 처리
            val errorMessage = e.toUserMessage(UserMessageCategory.AUTH)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    error = errorMessage,
                    isLoginSuccess = false
                )
            }
            sendSideEffect(OnboardingSideEffect.DomainError(errorMessage))
        }
    }

    private suspend fun logoutFromKakao(): Boolean =
        suspendCoroutine { continuation ->
            // 로그아웃
            UserApiClient.instance.logout { error ->
                if (error != null) {
                    Log.e("TAG", "로그아웃 실패. SDK에서 토큰 삭제됨", error)
                    continuation.resume(false)
                } else {
                    Log.i("TAG", "로그아웃 성공. SDK에서 토큰 삭제됨")
                    continuation.resume(true)
                }
            }
        }


    // 온보딩 완료여부 저장
    fun saveIsOnboardingCompleted(isOnboardingCompleted: Boolean) = viewModelScope.launch {
        saveIsOnboardingCompletedUseCase(isOnboardingCompleted)

        _uiState.update { currentState ->
            currentState.copy(
                isOnboardingCompleted = isOnboardingCompleted
            )
        }
    }

    // 닉네임 완료여부 저장
    fun saveIsNicknameSet(isNicknameSet: Boolean) = viewModelScope.launch {
        saveIsNicknameSetUseCase(isNicknameSet)

        _uiState.update { currentState ->
            currentState.copy(
                isNicknameSet = isNicknameSet
            )
        }
    }

    fun modifyHobbyTime(hobbyId: Long, minutes: Int) = viewModelScope.launch {  // 취미 정보 수정 - 시간
        flow {
            emit(modifyHobbyTimeUseCase(hobbyId, minutes))
        }.catch { throwable ->
            Timber.e("@@@@@@@@@@@@modifyHobbyTime@@@@@@@"+throwable)
            _sideEffectChannel.send(OnboardingSideEffect.Exception(throwable))
        }.collect { data ->
            Timber.e("@@@@@@@@@@@@modifyHobbyTime@@@@@@@"+data)
            if (data.status != 200) {
                _sideEffectChannel.send(OnboardingSideEffect.DomainError(data.data.message))
            }
        }
    }

    fun modifyHobbyExecutionCount(hobbyId: Long, executionCount: Int) = viewModelScope.launch {
        flow {
            emit(modifyHobbyExecutionCountUseCase(hobbyId, executionCount))
        }.catch { throwable ->
            Timber.e("@@@@@@@@@@@@modifyHobbyExecutionCount@@@@@@@"+throwable)
            _sideEffectChannel.send(OnboardingSideEffect.Exception(throwable))
        }.collect { data ->
            Timber.e("@@@@@@@@@@@@modifyHobbyExecutionCount@@@@@@@"+data)
            if (data.status != 200) {
                _sideEffectChannel.send(OnboardingSideEffect.DomainError(data.data.message))
            }
        }
    }

    fun modifyHobbyGoalDays(hobbyId: Long, goalDays: Boolean) = viewModelScope.launch {
        flow {
            emit(modifyHobbyDurationUseCase(hobbyId, goalDays))
        }.catch { throwable ->
            Timber.e("@@@@@@@@@@@@modifyHobbyGoalDays@@@@@@@" + throwable)
            _sideEffectChannel.send(OnboardingSideEffect.Exception(throwable))
        }.collect { data ->
            Timber.e("@@@@@@@@@@@@modifyHobbyExecutionCount@@@@@@@"+data)
            if (data.status != 200) {
                _sideEffectChannel.send(OnboardingSideEffect.DomainError(data.data.message))
            }
        }
    }

    fun logEvent(logEvent: String) {
        analyticsManager.logEvent(logEvent)
    }

}