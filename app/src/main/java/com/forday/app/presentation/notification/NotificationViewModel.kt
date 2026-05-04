package com.forday.app.presentation.notification

import androidx.lifecycle.viewModelScope
import com.forday.app.domain.usecase.GetNotificationsUseCase
import com.forday.app.presentation.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val getNotificationsUseCase: GetNotificationsUseCase
) : BaseViewModel<NotificationSideEffect>() {

    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState.toStateIn()

    fun loadNotifications() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        getNotificationsUseCase()
            .onSuccess { domain ->
                _uiState.update {
                    it.copy(
                        notifications = domain.notificationList,
                        showPermissionBanner = !domain.pushInfo.pushEnabled,
                        hasNext = domain.hasNext,
                        lastNotificationId = domain.lastNotificationId,
                        isLoading = false,
                    )
                }
            }
            .onFailure { throwable ->
                _uiState.update { it.copy(isLoading = false) }
                _sideEffectChannel.send(NotificationSideEffect.Exception(throwable))
            }
    }

    fun loadMore() = viewModelScope.launch {
        if (!_uiState.value.hasNext || _uiState.value.isPaginating) return@launch
        _uiState.update { it.copy(isPaginating = true) }
        getNotificationsUseCase(lastNotificationId = _uiState.value.lastNotificationId)
            .onSuccess { domain ->
                _uiState.update {
                    it.copy(
                        notifications = it.notifications + domain.notificationList,
                        hasNext = domain.hasNext,
                        lastNotificationId = domain.lastNotificationId,
                        isPaginating = false,
                    )
                }
            }
            .onFailure { throwable ->
                _uiState.update { it.copy(isPaginating = false) }
                _sideEffectChannel.send(NotificationSideEffect.Exception(throwable))
            }
    }

    fun onPermissionBannerClick() = viewModelScope.launch {
        _sideEffectChannel.send(NotificationSideEffect.OpenNotificationSettings)
    }
}
