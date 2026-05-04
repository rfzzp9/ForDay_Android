package com.forday.app.remote.model.response

import com.forday.app.data.model.ReactionSummaryFirstEntity
import com.forday.app.data.model.ReactionSummaryTabEntity
import com.forday.app.data.model.ReactionSummaryUserEntity
import com.forday.app.remote.RemoteMapper
import com.google.gson.annotations.SerializedName

data class ReactionSummaryFirstResponse(
    @SerializedName("status") val status: Int,
    @SerializedName("success") val isSuccess: Boolean,
    @SerializedName("data") val data: ReactionSummaryFirstDataResponse?
) : RemoteMapper<ReactionSummaryFirstEntity?> {
    override fun toData(): ReactionSummaryFirstEntity? = data?.toData()
}

data class ReactionSummaryFirstDataResponse(
    @SerializedName("recordId") val recordId: Int?,
    @SerializedName("reactionSummary") val reactionSummary: ReactionSummaryCountResponse?,
    @SerializedName("tabs") val tabs: Map<String, ReactionSummaryTabResponse>?
) {
    fun toData(): ReactionSummaryFirstEntity = ReactionSummaryFirstEntity(
        recordId = recordId ?: 0,
        reactionSummary = reactionSummary?.toData() ?: ReactionSummaryFirstEntity.SummaryCountEntity.EMPTY,
        tabs = tabs?.mapValues { it.value.toData() } ?: emptyMap()
    )
}

data class ReactionSummaryCountResponse(
    @SerializedName("totalCount") val totalCount: Int?,
    @SerializedName("awesome") val awesome: Int?,
    @SerializedName("great") val great: Int?,
    @SerializedName("amazing") val amazing: Int?,
    @SerializedName("fighting") val fighting: Int?
) {
    fun toData(): ReactionSummaryFirstEntity.SummaryCountEntity =
        ReactionSummaryFirstEntity.SummaryCountEntity(
            totalCount = totalCount ?: 0,
            awesome = awesome ?: 0,
            great = great ?: 0,
            amazing = amazing ?: 0,
            fighting = fighting ?: 0
        )
}

data class ReactionSummaryTabResponse(
    @SerializedName("users") val users: List<ReactionSummaryUserResponse>?,
    @SerializedName("lastReactionId") val lastReactionId: Long?,
    @SerializedName("lastUserId") val lastUserId: String?,
    @SerializedName("hasNext") val hasNext: Boolean?
) {
    fun toData(): ReactionSummaryTabEntity = ReactionSummaryTabEntity(
        users = users?.map { it.toData() } ?: emptyList(),
        lastReactionId = lastReactionId,
        hasNext = hasNext ?: false
    )
}

data class ReactionSummaryUserResponse(
    @SerializedName("reactionId") val reactionId: Long?,
    @SerializedName("userId") val userId: String?,
    @SerializedName("nickname") val nickname: String?,
    @SerializedName("profileImageUrl") val profileImageUrl: String?,
    @SerializedName("reactionType") val reactionType: String?
) {
    fun toData(): ReactionSummaryUserEntity = ReactionSummaryUserEntity(
        reactionId = reactionId ?: 0L,
        userId = userId.orEmpty(),
        nickname = nickname.orEmpty(),
        profileImageUrl = profileImageUrl.orEmpty(),
        reactionType = reactionType.orEmpty()
    )
}
