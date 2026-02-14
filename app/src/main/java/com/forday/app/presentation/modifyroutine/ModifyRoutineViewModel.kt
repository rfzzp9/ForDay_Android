package com.forday.app.presentation.modifyroutine

import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.forday.app.core.util.logAndExtractServerMessage
import com.forday.app.core.util.toUserMessage
import com.forday.app.domain.usecase.DeleteHobbyRoutineUseCase
import com.forday.app.domain.usecase.GetHobbyRoutineListUseCase
import com.forday.app.domain.usecase.ModifyHobbyRoutineUseCase
import com.forday.app.presentation.BaseViewModel
import com.forday.app.presentation.common.SnackbarManager
import com.forday.app.presentation.home.model.HomeState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ModifyRoutineViewModel @Inject constructor(
    private val getHobbyRoutineListUseCase: GetHobbyRoutineListUseCase,
    private val modifyHobbyRoutineUseCase: ModifyHobbyRoutineUseCase,
    private val deleteHobbyRoutineUseCase: DeleteHobbyRoutineUseCase,
    private val snackbarManager: SnackbarManager,
): BaseViewModel<ModifyRoutineSideEffect>() {

    private val _uiState: MutableStateFlow<RoutinesUiState> = MutableStateFlow(RoutinesUiState())
    val uiState: StateFlow<RoutinesUiState> = _uiState.toStateIn()

    fun fetchHobbyRoutineList(hobbyId: Long?) = viewModelScope.launch {  // 활동 리스트 조회
        flow {
            emit(getHobbyRoutineListUseCase(hobbyId))
        }.catch { throwable ->
            Timber.e("@####@#@#@#throwable "+throwable)
            val message = when (throwable) {
                is HttpException -> throwable.logAndExtractServerMessage(tag = "fetchHobbyRoutineList")
                else -> null
            }
            snackbarManager.show(message ?: throwable.toUserMessage())
        }.collect { data ->
            Timber.e("@####@#@#@#throwable "+data.data.routines.map { it.isAiRecommended })
            if (data.status == 200) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        routines = data.data.routines.toUiModelList()
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false) }
                snackbarManager.show(data.data.message)
            }
        }
    }

    fun modifyRoutine(routineId: Long, content: String) = viewModelScope.launch {
        flow {
            emit(modifyHobbyRoutineUseCase(routineId, content))
        }.catch { throwable ->
            val message = when (throwable) {
                is HttpException -> throwable.logAndExtractServerMessage(tag = "modifyRoutine")
                else -> null
            }
            snackbarManager.show(message ?: throwable.toUserMessage())
        }.collect { data ->
            if (data.status == 200) {
                _uiState.update { currentState ->
                    currentState.copy(
                        routines = currentState.routines.map { routine ->
                            if (routine.routineId == routineId) {
                                routine.copy(content = content)
                            } else {
                                routine
                            }
                        }
                    )
                }
            } else {
                snackbarManager.show(data.data.message)
            }
        }
    }

    fun deleteRoutine(routineId: Long) = viewModelScope.launch {
        flow {
            emit(deleteHobbyRoutineUseCase(routineId))
        }.catch { throwable ->
            Timber.e("deleteRoutine throwable : "+throwable)
            val message = when (throwable) {
                is HttpException -> throwable.logAndExtractServerMessage(tag = "deleteRoutine")
                else -> null
            }
            snackbarManager.show(message ?: throwable.toUserMessage())
        }.collect { data ->
            Timber.e("deleteRoutine data.data.message : "+data.data.message)
            if (data.status == 200) {
                // ✅ 성공 시 해당 routineId를 가진 항목 삭제
                _uiState.update { currentState ->
                    currentState.copy(
                        routines = currentState.routines.filter { routine ->
                            routine.routineId != routineId
                        }
                    )
                }
                snackbarManager.show("활동이 삭제되었어요.")
//                data.data.message  // 원래 코드 (서버에서 에러메세지 그대로 보내주면 다시 이 코드 원복하면 됨)
//                    .takeIf { it.isNotBlank() }
//                    ?.let(snackbarManager::show)
            } else {
                snackbarManager.show(data.data.message)
            }
        }
    }

}