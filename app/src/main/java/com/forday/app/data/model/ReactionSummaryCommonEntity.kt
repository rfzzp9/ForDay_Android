package com.forday.app.data.model

import com.forday.app.domain.model.ReactionSummaryTab
import com.forday.app.domain.model.ReactionSummaryUser

data class ReactionSummaryTabEntity(
    val users: List<ReactionSummaryUserEntity>,
    val lastReactionId: Long?,
    val hasNext: Boolean
) {
    fun toDomain(): ReactionSummaryTab = ReactionSummaryTab(
        users = users.map { it.toDomain() },
        lastReactionId = lastReactionId,
        hasNext = hasNext
    )
}

data class ReactionSummaryUserEntity(
    val reactionId: Long,
    val userId: String,
    val nickname: String,
    val profileImageUrl: String,
    val reactionType: String
) {
    fun toDomain(): ReactionSummaryUser = ReactionSummaryUser(
        reactionId = reactionId,
        userId = userId,
        nickname = nickname,
        profileImageUrl = profileImageUrl,
        reactionType = reactionType
    )
}
