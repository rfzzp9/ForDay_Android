package com.forday.app.presentation.allsettings

import androidx.lifecycle.viewModelScope
import com.forday.app.domain.repository.AuthRepository
import com.forday.app.core.util.UserMessageCategory
import com.forday.app.core.util.toUserMessage
import com.forday.app.presentation.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: AuthRepository
) : BaseViewModel<SettingsSideEffect>() {

    private val _uiState: MutableStateFlow<SettingsUiState> = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.toStateIn()

    fun logout() = viewModelScope.launch {
        repository.logout()
            .onSuccess { data ->
                if (data.isSuccess) {
                    _uiState.value = SettingsUiState(isLoggedOut = true)
                } else {
                    _sideEffectChannel.send(SettingsSideEffect.DomainError(data.message))
                }
            }
            .onFailure { throwable ->
                _sideEffectChannel.send(
                    SettingsSideEffect.DomainError(
                        throwable.toUserMessage(UserMessageCategory.AUTH)
                    )
                )
            }
    }

    fun cancelAccount() = viewModelScope.launch {
        repository.cancelAccount()
            .onSuccess {
                _uiState.value = SettingsUiState(isAccountCancelled = true)
            }
            .onFailure { throwable ->
                _sideEffectChannel.send(
                    SettingsSideEffect.DomainError(
                        throwable.toUserMessage(UserMessageCategory.AUTH)
                    )
                )
            }
    }
}