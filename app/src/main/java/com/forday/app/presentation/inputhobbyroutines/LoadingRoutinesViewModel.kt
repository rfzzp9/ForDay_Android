package com.forday.app.presentation.inputhobbyroutines

import androidx.lifecycle.viewModelScope
import com.forday.app.domain.usecase.GetUserNicknameUseCase
import com.forday.app.presentation.BaseViewModel
import com.forday.app.presentation.common.SnackbarManager
import com.forday.app.presentation.httpCatch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoadingRoutinesState(
    val nickname: String? = null,
)

@HiltViewModel
class LoadingRoutinesViewModel @Inject constructor(
    private val getUserNicknameUseCase: GetUserNicknameUseCase,
    private val snackbarManager: SnackbarManager,
) : BaseViewModel<Nothing>() {

    private val _uiState = MutableStateFlow(LoadingRoutinesState())
    val uiState: StateFlow<LoadingRoutinesState> = _uiState.toStateIn()

    fun getUserNickname() = viewModelScope.launch {
        getUserNicknameUseCase()
            .httpCatch(tag = "getUserNickname") { errorData ->
                snackbarManager.show(errorData.message)
            }.collect { data ->
                _uiState.update { it.copy(nickname = data) }
            }
    }
}
