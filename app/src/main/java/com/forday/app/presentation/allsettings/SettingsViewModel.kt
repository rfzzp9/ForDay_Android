package com.forday.app.presentation.allsettings

import androidx.lifecycle.viewModelScope
import com.forday.app.domain.repository.AuthRepository
import com.forday.app.core.util.UserMessageCategory
import com.forday.app.core.util.toUserMessage
import com.forday.app.domain.usecase.GetNotificationToggleStatusUseCase
import com.forday.app.domain.usecase.ToggleNotificationUseCase
import com.forday.app.presentation.BaseViewModel
import com.forday.app.presentation.common.SnackbarManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val snackbarManager: SnackbarManager,
    private val toggleNotificationUseCase: ToggleNotificationUseCase,
    private val getNotificationToggleStatusUseCase: GetNotificationToggleStatusUseCase,
) : BaseViewModel<SettingsSideEffect>() {

    private val _uiState: MutableStateFlow<SettingsUiState> = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.toStateIn()

    init {
        loadNotificationStatus()
    }

    fun loadNotificationStatus() = viewModelScope.launch {
        getNotificationToggleStatusUseCase()
            .onSuccess { domain ->
                _uiState.update {
                    it.copy(
                        postLikeNotificationEnabled = domain.recordPushEnabled,
                        pushNotificationEnabled = domain.appPushEnabled,
                    )
                }
            }
            .onFailure { throwable ->
                throwable.printStackTrace()
            }
    }

    fun togglePostLikeNotification(active: Boolean) = viewModelScope.launch {
        val previous = _uiState.value.postLikeNotificationEnabled
        _uiState.update { it.copy(postLikeNotificationEnabled = active) }
        toggleNotificationUseCase(active, "RECORD")
            .onFailure { throwable ->
                _uiState.update { it.copy(postLikeNotificationEnabled = previous) }
                snackbarManager.show(throwable.toUserMessage(UserMessageCategory.AUTH))
            }
    }

    fun togglePushNotification(active: Boolean) = viewModelScope.launch {
        val previous = _uiState.value.pushNotificationEnabled
        _uiState.update { it.copy(pushNotificationEnabled = active) }
        toggleNotificationUseCase(active, "APP")
            .onFailure { throwable ->
                _uiState.update { it.copy(pushNotificationEnabled = previous) }
                snackbarManager.show(throwable.toUserMessage(UserMessageCategory.AUTH))
            }
    }

    fun logout() = viewModelScope.launch {
        repository.logout()
            .onSuccess { data ->
                if (data.isSuccess) {
                    _sideEffectChannel.send(SettingsSideEffect.LoggedOut)
                } else {
                    snackbarManager.show(data.message)
                }
            }
            .onFailure { throwable ->
                throwable.printStackTrace()
                snackbarManager.show(throwable.toUserMessage(UserMessageCategory.AUTH))
            }
    }

    fun cancelAccount() = viewModelScope.launch {
        repository.cancelAccount()
            .onSuccess {
                _sideEffectChannel.send(SettingsSideEffect.AccountCancelled)
            }
            .onFailure { throwable ->
                throwable.printStackTrace()
                snackbarManager.show(throwable.toUserMessage(UserMessageCategory.AUTH))
            }
    }
}