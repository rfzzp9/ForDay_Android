package com.forday.app.presentation.record

interface RecordRoutineSideEffect {
    data class DomainError(val error: String): RecordRoutineSideEffect
    data class Exception(val error: Throwable): RecordRoutineSideEffect
}