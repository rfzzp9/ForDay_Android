package com.forday.app.presentation.modifyroutine

import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.forday.app.domain.usecase.DeleteHobbyRoutineUseCase
import com.forday.app.domain.usecase.GetHobbyRoutineListUseCase
import com.forday.app.domain.usecase.ModifyHobbyRoutineUseCase
import com.forday.app.presentation.BaseViewModel
import com.forday.app.presentation.home.model.HomeState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ModifyRoutineViewModel @Inject constructor(
    private val getHobbyRoutineListUseCase: GetHobbyRoutineListUseCase,
    private val modifyHobbyRoutineUseCase: ModifyHobbyRoutineUseCase,
    private val deleteHobbyRoutineUseCase: DeleteHobbyRoutineUseCase,
): BaseViewModel<ModifyRoutineSideEffect>() {

    private val _uiState: MutableStateFlow<RoutinesUiState> = MutableStateFlow(RoutinesUiState())
    val uiState: StateFlow<RoutinesUiState> = _uiState.toStateIn()

    fun fetchHobbyRoutineList(hobbyId: Long?) = viewModelScope.launch {  // 활동 리스트 조회
        flow {
            emit(getHobbyRoutineListUseCase(hobbyId))
        }.catch { throwable ->
            Timber.e("@####@#@#@#throwable "+throwable)
            _sideEffectChannel.send(ModifyRoutineSideEffect.Exception(throwable))
        }.collect { data ->
            Timber.e("@####@#@#@#throwable "+data.data.routines.map { it.isAiRecommended })
            _uiState.update {
                it.copy(
                    isLoading = false,
                    routines = data.data.routines.toUiModelList()
                )
            }
        }
    }

    fun modifyRoutine(routineId: Long, content: String) = viewModelScope.launch {
        flow {
            emit(modifyHobbyRoutineUseCase(routineId, content))
        }.catch { throwable ->
            _sideEffectChannel.send(ModifyRoutineSideEffect.Exception(throwable))
        }.collect { data ->
            if (data.status == 200) {
                // ✅ 성공 시 해당 routineId의 content 업데이트
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
                _sideEffectChannel.send(ModifyRoutineSideEffect.DomainError(data.data.message))
            }
        }
    }

    fun deleteRoutine(routineId: Long) = viewModelScope.launch {
        flow {
            emit(deleteHobbyRoutineUseCase(routineId))
        }.catch { throwable ->
            Timber.e("deleteRoutine throwable : "+throwable)
            _sideEffectChannel.send(ModifyRoutineSideEffect.Exception(throwable))
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
            } else {
                _sideEffectChannel.send(ModifyRoutineSideEffect.DomainError(data.data.message))
            }
        }
    }

}