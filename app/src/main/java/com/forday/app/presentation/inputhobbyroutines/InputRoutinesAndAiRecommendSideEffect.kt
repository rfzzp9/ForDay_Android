package com.forday.app.presentation.inputhobbyroutines

sealed interface InputRoutinesAndAiRecommendSideEffect {
    data class DomainError(val error: String): InputRoutinesAndAiRecommendSideEffect
    data class Exception(val error: Throwable): InputRoutinesAndAiRecommendSideEffect
}