package com.forday.app.presentation.inputhobbyroutines

import androidx.lifecycle.viewModelScope
import com.forday.app.core.logger.analytics.AnalyticsEvent
import com.forday.app.core.logger.analytics.AnalyticsEvents
import com.forday.app.core.logger.analytics.AnalyticsManager
import com.forday.app.domain.usecase.CreateRoutinesUseCase
import com.forday.app.domain.usecase.GetHobbyMateRoutinesUseCase
import com.forday.app.domain.usecase.GetOnboardingDataUseCase
import com.forday.app.presentation.BaseViewModel
import com.forday.app.presentation.common.SnackbarManager
import com.forday.app.presentation.httpCatch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class InputRoutinesAndAiRecommendViewModel @Inject constructor(
    private val analyticsManager: AnalyticsManager,
    private val getHobbyMateRoutines: GetHobbyMateRoutinesUseCase,
    private val createRoutinesUseCase: CreateRoutinesUseCase,
    private val getOnboardingDataUseCase: GetOnboardingDataUseCase,
    private val snackbarManager: SnackbarManager,
) : BaseViewModel<InputRoutinesAndAiRecommendSideEffect>() {

    private val _uiState: MutableStateFlow<RoutinesState> = MutableStateFlow(RoutinesState())
    val uiState: StateFlow<RoutinesState> = _uiState.toStateIn()

    init {
        getOnboardingData()
    }

    fun searchHobbyMatesRoutines(selectedHobbyId: Long?) = viewModelScope.launch {  // 나와 취미가 비슷한 사람들의 루틴 추천
            flow {
                emit(getHobbyMateRoutines(selectedHobbyId))
            }.httpCatch(tag = "searchHobbyMatesRoutines") { errorData ->
                snackbarManager.show(errorData.message)
            }.collect { result ->
                _uiState.update {
                    it.copy(
                        hobbymateRoutines = result.data.activities.map { it.content },
                        isLoading = false
                    )
                }
            }
        }

    fun initHobbyName(hobbyName: String?) {
        _uiState.update { it.copy(selectedHobbyName = hobbyName) }
    }

    private fun getOnboardingData() = viewModelScope.launch {
        getOnboardingDataUseCase()
            .httpCatch(tag = "getOnboardingData") { errorData ->
                snackbarManager.show(errorData.message)
            }
            .collect { onboardingData ->
                _uiState.update {
                    it.copy(
                        selectedHobbyName = onboardingData.hobbyName,
                    )
                }
            }
    }

    fun createRoutines(hobbyId: Long?, routineList: List<Pair<Boolean, String>>) =  // 취미 활동 생성
        viewModelScope.launch {
            flow {
                emit(createRoutinesUseCase.invoke(hobbyId, routineList))
            }.httpCatch(tag = "createRoutines") { errorData ->
                snackbarManager.show(errorData.message)
            }.collect { result ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        routineId = result.data.createdRoutineNum
                    )
                }
                routineList.forEach { (isAi, activityName) ->
                    logEvent(AnalyticsEvents.activityAdded(
                        entryPoint = "activity_list_plus",
                        source = if (isAi) "ai_recommendation" else "manual",
                        hobbyName = _uiState.value.selectedHobbyName,
                        activityName = activityName
                    ))
                }
                _sideEffectChannel.send(InputRoutinesAndAiRecommendSideEffect.CreateRoutinesSuccess)
            }
        }

    fun logEvent(logEvent: String) {
        analyticsManager.logEvent(logEvent)
    }

    fun logEvent(event: AnalyticsEvent) {
        analyticsManager.logEvent(event)
    }

}
