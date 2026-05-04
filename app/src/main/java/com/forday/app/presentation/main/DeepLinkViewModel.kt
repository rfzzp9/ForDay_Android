package com.forday.app.presentation.main

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class DeepLinkViewModel @Inject constructor() : ViewModel() {

    private val _pendingRecordId = MutableStateFlow<Long?>(null)
    val pendingRecordId: StateFlow<Long?> = _pendingRecordId.asStateFlow()

    private val _pendingNotificationId = MutableStateFlow<Long?>(null)
    val pendingNotificationId: StateFlow<Long?> = _pendingNotificationId.asStateFlow()

    fun setPendingDeepLink(recordId: Long, notificationId: Long?) {
        _pendingRecordId.value = recordId
        _pendingNotificationId.value = notificationId
    }

    fun consumePendingDeepLink() {
        _pendingRecordId.value = null
        _pendingNotificationId.value = null
    }
}
