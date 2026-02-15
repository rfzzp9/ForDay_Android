package com.forday.app.core.designsystem.component.state

data class ErrorDataUiState(
    val message: String,
    val errorType: ErrorType,
) {

    enum class ErrorType {
        TYPE_RETRY,
        TYPE_BACK,
    }
}