package com.forday.app.presentation.home

sealed interface HomeSideEffect {
    data class DomainError(val error: String): HomeSideEffect
    data class Exception(val error: Throwable): HomeSideEffect
}