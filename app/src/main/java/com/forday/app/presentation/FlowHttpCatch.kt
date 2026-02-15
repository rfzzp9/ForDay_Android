package com.forday.app.presentation

import com.forday.app.core.designsystem.component.state.ErrorDataUiState
import com.forday.app.core.util.logAndExtractServerErrorBody
import com.forday.app.core.util.toUserMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import retrofit2.HttpException

inline fun <T> Flow<T>.httpCatch(
    crossinline body: (ErrorDataUiState) -> Unit,
): Flow<T> {
    return this.catch { throwable ->
        val message = when (throwable) {
            is HttpException -> throwable.logAndExtractServerErrorBody(tag = "fetchHobbyRoutineList")
            else -> null
        }

        val errorType = when {
            throwable is HttpException && throwable.code() in 400..499 -> ErrorDataUiState.ErrorType.TYPE_BACK
            else -> ErrorDataUiState.ErrorType.TYPE_RETRY
        }

        body(
            ErrorDataUiState(
                message = message?.data?.message ?: throwable.toUserMessage(),
                errorType,
            )
        )
    }
}
