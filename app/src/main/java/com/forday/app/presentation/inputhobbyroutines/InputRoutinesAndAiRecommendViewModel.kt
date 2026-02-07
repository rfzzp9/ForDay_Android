package com.forday.app.presentation.inputhobbyroutines


import androidx.lifecycle.viewModelScope
import com.forday.app.core.datastore.UserLocalDataSource
import com.forday.app.core.logger.analytics.AnalyticsManager
import com.forday.app.domain.usecase.CreateRoutinesUseCase
import com.forday.app.domain.usecase.GetAiRecommendedRoutinesUseCase
import com.forday.app.domain.usecase.GetHobbyMateRoutinesUseCase
import com.forday.app.domain.usecase.GetOnboardingDataUseCase
import com.forday.app.domain.usecase.GetUserNicknameUseCase
import com.forday.app.presentation.BaseViewModel
import com.forday.app.presentation.home.HomeSideEffect
import com.forday.app.presentation.onboarding.OnboardingSideEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class InputRoutinesAndAiRecommendViewModel @Inject constructor(
    private val analyticsManager: AnalyticsManager,
    private val getHobbyMateRoutines: GetHobbyMateRoutinesUseCase,
    private val createRoutinesUseCase: CreateRoutinesUseCase,
    private val getAiRecommendedRoutinesUseCase: GetAiRecommendedRoutinesUseCase,
    private val getOnboardingDataUseCase: GetOnboardingDataUseCase,
    private val getUserNicknameUseCase: GetUserNicknameUseCase,
    private val userLocalDataSource: UserLocalDataSource  //UT 테스트용 나중에 지우기 todo
) : BaseViewModel<InputRoutinesAndAiRecommendSideEffect>() {

    private val _uiState: MutableStateFlow<RoutinesState> = MutableStateFlow(RoutinesState())
    val uiState: StateFlow<RoutinesState> = _uiState.toStateIn()

    init {
        getOnboardingData()
    }

    fun searchHobbyMatesRoutines(selectedHobbyId: Long?) =
        viewModelScope.launch {  // 나와 취미가 비슷한 사람들의 루틴 추천
            flow {
                emit(getHobbyMateRoutines(selectedHobbyId))
            }.catch { throwable ->
                Timber.e("@#@나와 취미가 비슷한 사람들의 루틴 추천#@#@ " + throwable)
                _sideEffectChannel.send(InputRoutinesAndAiRecommendSideEffect.Exception(throwable))
            }.collect { result ->
                result.data.activities.map { Timber.e("@#@나와 취미가 비슷한 사람들의 루틴 추천#@#@ " + it.content) }
                _uiState.update {
                    it.copy(
                        hobbymateRoutines = result.data.activities.map { it.content },
                        isLoading = false
                    )
                }
            }
        }

    fun resetInputState() {
        _uiState.update { it.copy(selectedAiRoutine = null) }
    }

    private fun getOnboardingData() = viewModelScope.launch {
        getOnboardingDataUseCase()
            .catch { throwable ->
                _sideEffectChannel.send(InputRoutinesAndAiRecommendSideEffect.Exception(throwable))
            }
            .collect { onboardingData ->
                _uiState.update {
                    it.copy(
//                        hobbyId = onboardingData.hobbyId,
                        selectedHobbyName = onboardingData.hobbyName,
                    )
                }
            }
    }

    fun createRoutines(hobbyId: Long?, routineList: List<Pair<Boolean, String>>) = // TODO 취미활동 생성 시 AI 추천이 계속 FALSE로 전달되는 오류 수정해야 함

        viewModelScope.launch {
            flow {
                emit(createRoutinesUseCase.invoke(hobbyId, routineList))
            }.catch { throwable ->
                routineList.map { Timber.e("@#@#@#@#@#@$#A$#ARDA "+it.component1()+", "+it.component2()) }
                Timber.e("@@@@@@@throwablethrowable@@@@@@@@ "+throwable)
                Timber.e(throwable)
                _sideEffectChannel.send(InputRoutinesAndAiRecommendSideEffect.Exception(throwable))
            }.collect { result ->
                routineList.map { Timber.e("@#@#@#@#@#@$#A$#ARDA "+it.component1()+", "+it.component2()) }
                Timber.e("@@@@@@@throwablethrowable@@@@@@@@ "+result.data)
                _uiState.update {
                    it.copy(
                        isCreateRoutines = result.isSuccess,
                        isLoading = false,
                        routineId = result.data.createdRoutineNum  // 취미활동 번호
                    )
                }
            }
        }

    fun getAiRecommendedRoutines(hobbyId: Long?) = viewModelScope.launch {
        Timber.d("🔵 AI routines 요청 시작: hobbyId=%s", hobbyId)

        // ✅ 로딩 상태 먼저 설정
        _uiState.update { it.copy(isLoading = true) }

        flow {
            emit(getAiRecommendedRoutinesUseCase(hobbyId))
        }.catch { throwable ->
            Timber.d("🔵 AI routines 요청 시작: hobbyId=%s", throwable)
            when (throwable) {
                is HttpException -> {
                    val errorBody = throwable.response()?.errorBody()?.string()
                    Timber.e(
                        throwable,
                        "[AI routines] HttpException code=%d message=%s body=%s",
                        throwable.code(),
                        throwable.message(),
                        errorBody
                    )
                }
                is IOException -> Timber.e(throwable, "[AI routines] 네트워크 I/O 오류")
                else -> Timber.e(throwable, "[AI routines] 예기치 못한 오류")
            }
            // ✅ 에러 시에도 로딩 상태 해제
            _uiState.update { it.copy(isLoading = false) }
            _sideEffectChannel.send(InputRoutinesAndAiRecommendSideEffect.Exception(throwable))
        }.collect { result ->
            Timber.d("🟢 AI routines 성공: count=%d", result.data.routines.size)

            val newAiRoutines = result.data.routines.map { it.toPresentation() }

            // ✅ 상세 로그 추가
            Timber.d("🟢 변환된 루틴들: ${newAiRoutines.map { it.content }}")

            // ✅ 명시적으로 새 리스트 생성 (방어적 복사)
            _uiState.update { currentState ->
                Timber.d("🔄 State 업데이트 전: ${currentState.aiRoutineList.size}개")
                val updated = currentState.copy(
                    aiRoutineList = newAiRoutines.toList(),  // 명시적 복사
                    aiCallCount = result.data.aiCallCount,
                    recommendedText = result.data.recommendedText,
                    isLoading = false
                )
                Timber.d("🔄 State 업데이트 후: ${updated.aiRoutineList.size}개")
                updated
            }
        }
    }

    fun getUserNickname() = viewModelScope.launch {
        getUserNicknameUseCase()
            .catch { throwable ->
                _sideEffectChannel.send(InputRoutinesAndAiRecommendSideEffect.Exception(throwable))
            }.collect { data ->
                _uiState.update { state ->
                    state.copy(
                        nickname = data
                    )
                }
            }
    }

    fun removeAiList() {
        _uiState.update {
            it.copy(
                aiRoutineList = emptyList(),
            )
        }
    }

    fun setSelectedAiRoutine(routine: AiRoutineItemState) {
        _uiState.update { it.copy(selectedAiRoutine = routine) }
    }

    fun clearSelectedAiRoutine() {
        _uiState.update { it.copy(selectedAiRoutine = null) }
    }

    suspend fun getHobbyId(): Flow<Long> = //TODO UT 테스트
        userLocalDataSource.getHobbyId()

    fun logEvent(logEvent: String) {
        analyticsManager.logEvent(logEvent)
    }

}