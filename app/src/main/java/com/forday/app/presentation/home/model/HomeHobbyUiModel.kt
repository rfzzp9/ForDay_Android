package com.forday.app.presentation.home.model

data class HomeHobbyUiModel(
    val inProgressHobbies: List<InProgressHobbyUiModel>,
    val routinePreview: RoutinePreviewUiModel?,
    val greetingMessage: String,
    val userSummaryText: String,
    val recommendMessage: String,
    val aiCallRemaining: Boolean,
    val aiCallRemainingCount: Int?,
    val unReadNotificationExists: Boolean = false
)

data class InProgressHobbyUiModel(
    val hobbyId: Long? = null,
    val name: String,
    val isCurrent: Boolean
)

data class RoutinePreviewUiModel(
    val routineId: Int,
    val content: String,
    val isAiRecommended: Boolean
)