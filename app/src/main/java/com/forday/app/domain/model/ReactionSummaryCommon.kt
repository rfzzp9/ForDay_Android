package com.forday.app.domain.model

data class ReactionSummaryTab(
    val users: List<ReactionSummaryUser>,
    val lastReactionId: Long?,
    val hasNext: Boolean
)

data class ReactionSummaryUser(
    val reactionId: Long,
    val userId: String,
    val nickname: String,
    val profileImageUrl: String,
    val reactionType: String
)
