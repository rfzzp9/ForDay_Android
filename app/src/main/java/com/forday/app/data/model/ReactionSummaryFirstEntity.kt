package com.forday.app.data.model

import com.forday.app.data.DataMapper
import com.forday.app.domain.model.ReactionSummaryFirstDomain

data class ReactionSummaryFirstEntity(
    val recordId: Int,
    val reactionSummary: SummaryCountEntity,
    val tabs: Map<String, ReactionSummaryTabEntity>
) : DataMapper<ReactionSummaryFirstDomain> {

    override fun toDomain(): ReactionSummaryFirstDomain = ReactionSummaryFirstDomain(
        recordId = recordId,
        reactionSummary = reactionSummary.toDomain(),
        tabs = tabs.mapValues { it.value.toDomain() }
    )

    data class SummaryCountEntity(
        val totalCount: Int,
        val awesome: Int,
        val great: Int,
        val amazing: Int,
        val fighting: Int
    ) {
        fun toDomain(): ReactionSummaryFirstDomain.SummaryCount = ReactionSummaryFirstDomain.SummaryCount(
            totalCount = totalCount,
            awesome = awesome,
            great = great,
            amazing = amazing,
            fighting = fighting
        )

        companion object {
            val EMPTY = SummaryCountEntity(0, 0, 0, 0, 0)
        }
    }
}
