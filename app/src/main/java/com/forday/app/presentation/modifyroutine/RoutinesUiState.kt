package com.forday.app.presentation.modifyroutine

data class RoutinesUiState(
    val isLoading: Boolean = false,
    val routines: List<RoutineUiModel> = emptyList(),
    val error: String? = null
)

data class RoutineUiModel(
    val routineId: Long,
    val content: String,
    val isAiRecommended: Boolean,
    val isDeletable: Boolean,
    val collectedStickerNum: Int,
)
