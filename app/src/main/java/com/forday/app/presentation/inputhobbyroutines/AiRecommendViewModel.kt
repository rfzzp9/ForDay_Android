package com.forday.app.presentation.inputhobbyroutines

import androidx.lifecycle.viewModelScope
import com.forday.app.core.datastore.UserLocalDataSource
import com.forday.app.core.logger.analytics.AnalyticsEvent
import com.forday.app.core.logger.analytics.AnalyticsManager
import com.forday.app.domain.usecase.GetAiRecommendedRoutinesAgainUseCase
import com.forday.app.domain.usecase.GetAiRecommendedRoutinesUseCase
import com.forday.app.presentation.BaseViewModel
import com.forday.app.presentation.common.SnackbarManager
import com.forday.app.presentation.httpCatch
import com.forday.app.core.designsystem.component.state.ErrorDataUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AiRecommendState(
    val aiRoutineList: List<AiRoutineItemState> = emptyList(),
    val aiCallCount: Int = 0,
    val recommendedText: String = "",
    val isLoading: Boolean = false,
    val errorData: ErrorDataUiState? = null,
)

@HiltViewModel
class AiRecommendViewModel @Inject constructor(
    private val analyticsManager: AnalyticsManager,
    private val getAiRecommendedRoutinesUseCase: GetAiRecommendedRoutinesUseCase,
    private val getAiRecommendedRoutinesAgainUseCase: GetAiRecommendedRoutinesAgainUseCase,
    private val userLocalDataSource: UserLocalDataSource,
    private val snackbarManager: SnackbarManager,
) : BaseViewModel<Nothing>() {

    private val _uiState = MutableStateFlow(AiRecommendState())
    val uiState: StateFlow<AiRecommendState> = _uiState.toStateIn()

    fun getAiRecommendedRoutines(hobbyId: Long?) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        flow {
            emit(getAiRecommendedRoutinesUseCase(hobbyId))
        }.httpCatch(tag = "getAiRecommendedRoutines") { errorData ->
            when (errorData.errorClassName) {
                "AI_CALL_LIMIT_EXCEEDED" -> getAiRecommendedRoutinesAgain(hobbyId)
                else -> _uiState.update { it.copy(isLoading = false, errorData = errorData) }
            }
        }.collect { data ->
            _uiState.update {
                it.copy(
                    aiRoutineList = data.data.routines.map { it.toPresentation() },
                    aiCallCount = data.data.aiCallCount,
                    recommendedText = data.data.recommendedText,
                    isLoading = false,
                    errorData = null
                )
            }
        }
    }

    fun getAiRecommendedRoutinesAgain(hobbyId: Long?, type: String? = "LATEST") = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        flow {
            emit(getAiRecommendedRoutinesAgainUseCase(hobbyId, type))
        }.httpCatch(tag = "getAiRecommendedRoutinesAgain") { errorData ->
            _uiState.update { it.copy(isLoading = false, errorData = errorData) }
        }.collect { data ->
            _uiState.update {
                it.copy(
                    aiRoutineList = data.data.activityItems.map { item ->
                        AiRoutineItemState(
                            routineId = item.itemId,
                            topic = "",
                            content = item.content,
                            description = item.description
                        )
                    },
                    recommendedText = data.data.message,
                    isLoading = false,
                    errorData = null
                )
            }
        }
    }

    fun saveAiRoutines(routines: List<AiRoutineItemState>) = viewModelScope.launch {
        userLocalDataSource.saveAiRoutineList(routines)
    }

    fun logEvent(event: AnalyticsEvent) {
        analyticsManager.logEvent(event)
    }

    fun logEvent(logEvent: String) {
        analyticsManager.logEvent(logEvent)
    }
}
