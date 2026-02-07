package com.forday.app.domain.model

data class ReactionDetailDomain(
    val status: Int,
    val isSuccess: Boolean,
    val reactionType: String,
    val users: List<ReactionUserInfo>,
    val message: String,
    val errorClassName: String
)

data class ReactionUserInfo(
    val userId: String,
    val nickname: String,
    val profileImageUrl: String?,
    val reactedAt: String
)