package com.forday.app.remote.model.response

import com.forday.app.data.model.ReactionSummaryMoreEntity
import com.forday.app.data.model.ReactionSummaryTabEntity
import com.forday.app.data.model.ReactionSummaryUserEntity
import com.forday.app.remote.RemoteMapper
import com.google.gson.annotations.SerializedName

data class ReactionSummaryMoreResponse(
    @SerializedName("status") val status: Int,
    @SerializedName("success") val isSuccess: Boolean,
    @SerializedName("data") val data: ReactionSummaryMoreDataResponse?
) : RemoteMapper<ReactionSummaryMoreEntity?> {
    override fun toData(): ReactionSummaryMoreEntity? = data?.toData()
}

data class ReactionSummaryMoreDataResponse(
    @SerializedName("tabs") val tabs: Map<String, ReactionSummaryTabResponse>?
) {
    fun toData(): ReactionSummaryMoreEntity = ReactionSummaryMoreEntity(
        tabs = tabs?.mapValues { it.value.toData() } ?: emptyMap()
    )
}
