package com.forday.app.presentation.modifyroutine

interface ModifyRoutineSideEffect {
    data class DomainError(val error: String): ModifyRoutineSideEffect
    data class Exception(val error: Throwable): ModifyRoutineSideEffect
}