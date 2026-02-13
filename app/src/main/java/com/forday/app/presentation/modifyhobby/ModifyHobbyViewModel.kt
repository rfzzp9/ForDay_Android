package com.forday.app.presentation.modifyhobby

import androidx.lifecycle.viewModelScope
import com.forday.app.core.logger.analytics.AnalyticsManager
import com.forday.app.domain.usecase.ChangeHobbyStatusUseCase
import com.forday.app.domain.usecase.GetMyHobbyListUseCase
import com.forday.app.domain.usecase.ModifyHobbyTimeUseCase
import com.forday.app.presentation.BaseViewModel
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
class ModifyHobbyViewModel @Inject constructor(
    private val analyticsManager: AnalyticsManager,
    private val getMyHobbyListUseCase: GetMyHobbyListUseCase,   // 내 취미 설정 페이지 조회
    private val changeHobbyStatusUseCase: ChangeHobbyStatusUseCase  // 취미 보관 또는 꺼내기


) : BaseViewModel<ModifyHobbySideEffect>() {

    private val _uiState: MutableStateFlow<ModifyHobbyUiState> = MutableStateFlow(ModifyHobbyUiState())
    val uiState: StateFlow<ModifyHobbyUiState> = _uiState.toStateIn()

    init {
        fetchMyHobbyList(null)  // 처음 조회 시 null로 보내면 진행중인 취미 조회
    }

    fun fetchMyHobbyList(inProgress: String?) = viewModelScope.launch {  // 내 취미 설정 페이지 조회
        _uiState.update { it.copy(isLoading = true) }

        flow {
            emit(getMyHobbyListUseCase(inProgress).data)
        }.catch { throwable ->
            throwable.printStackTrace()
            Timber.e("@@@@@@@@@@@@@@@@@@@"+throwable)
            _sideEffectChannel.send(ModifyHobbySideEffect.Exception(throwable))
        }.collect { data ->
            Timber.e("@@@@@@@@@@@@@@@@@@@data  :::  "+data)
            _uiState.update { state ->
                state.copy(
                    isLoading = false,
                    currentHobbyStatus = data.currentHobbyStatus,
                    inProgressHobbyCount = data.inProgressHobbyCount,
                    archivedHobbyCount = data.archivedHobbyCount,
                    hobbies = data.hobbies.map { it.toPresentation() }
                )
            }
        }

    }

    fun modifyHobbyStatus(hobbyId: Long, hobbyStatus: String) = viewModelScope.launch {
        flow {
            emit(changeHobbyStatusUseCase(hobbyId, hobbyStatus))
        }.catch { throwable ->
            throwable.printStackTrace()
            Timber.e("@@@@@@@@@@@@@@@@@@@"+throwable)
            _sideEffectChannel.send(ModifyHobbySideEffect.Exception(throwable))
        }.collect { data ->
            Timber.e("@@@@@@@@@@@@@@@@@@@"+data)
            if (data.status != 200) {
                _sideEffectChannel.send(ModifyHobbySideEffect.DomainError(data.data.message))
            } else {
                // ✅ 성공 시 현재 보고 있는 탭의 목록 재조회
                fetchMyHobbyList(_uiState.value.currentHobbyStatus)
                // 또는 양쪽 탭 모두 재조회 (카운트 업데이트)
                // fetchMyHobbyList(HobbyStatus.IN_PROGRESS.name)
            }
        }
    }

    fun logEvent(logEvent: String) {
        analyticsManager.logEvent(logEvent)
    }
}