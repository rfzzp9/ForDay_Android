package com.forday.app.presentation.common

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SnackbarManager @Inject constructor() {

    private val _sideEffects = MutableSharedFlow<AppSideEffect>(
        extraBufferCapacity = 64
    )
    val sideEffects: SharedFlow<AppSideEffect> = _sideEffects.asSharedFlow()

    fun show(message: String) {
        if (message.isBlank()) return
        _sideEffects.tryEmit(AppSideEffect.ShowSnackbar(message))
    }
}
