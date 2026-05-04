package com.forday.app.data.model

import com.forday.app.data.DataMapper
import com.forday.app.domain.model.ReactionSummaryMoreDomain

data class ReactionSummaryMoreEntity(
    val tabs: Map<String, ReactionSummaryTabEntity>
) : DataMapper<ReactionSummaryMoreDomain> {

    override fun toDomain(): ReactionSummaryMoreDomain = ReactionSummaryMoreDomain(
        tabs = tabs.mapValues { it.value.toDomain() }
    )
}
