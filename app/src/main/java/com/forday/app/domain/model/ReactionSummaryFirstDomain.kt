package com.forday.app.domain.model

data class ReactionSummaryFirstDomain(
    val recordId: Int,
    val reactionSummary: SummaryCount,
    val tabs: Map<String, ReactionSummaryTab>
) {
    data class SummaryCount(
        val totalCount: Int,
        val awesome: Int,
        val great: Int,
        val amazing: Int,
        val fighting: Int
    )
}
