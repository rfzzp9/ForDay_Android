package com.forday.app.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

open class BaseViewModel<SIDE_EFFECT>: ViewModel() {

    @Suppress("PropertyName")
    protected val _sideEffectChannel = Channel<SIDE_EFFECT>(Channel.BUFFERED)
    val sideEffect = _sideEffectChannel.receiveAsFlow()

    protected fun CoroutineExceptionHandler(block: suspend (CoroutineContext, Throwable) -> Unit): CoroutineExceptionHandler =
        kotlinx.coroutines.CoroutineExceptionHandler { coroutineContext, throwable ->
            viewModelScope.launch {
                block(coroutineContext, throwable)
            }
        }

    @Suppress("FunctionName")
    protected inline fun <reified T: Any> MutableUiStateFlow(block: () -> T): MutableStateFlow<T> {
        return MutableStateFlow(block())
    }

    protected inline fun <reified T: Any> MutableStateFlow<T>.toStateIn(
        scope: CoroutineScope = viewModelScope,
        started: SharingStarted = SharingStarted.WhileSubscribed(5_000),
    ): StateFlow<T> = stateIn(
        scope = scope,
        started = started,
        initialValue = this.value
    )

    override fun onCleared() {
        super.onCleared()
        _sideEffectChannel.close()
    }

}