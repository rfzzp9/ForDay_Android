package com.forday.app.presentation.onboarding

sealed interface OnboardingSideEffect {
    data class DomainError(val error: String): OnboardingSideEffect
    data class Exception(val error: Throwable): OnboardingSideEffect
    data object OnClose: OnboardingSideEffect
}