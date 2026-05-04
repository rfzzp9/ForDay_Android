package com.forday.app.domain.model

data class HobbyChipsDomain(
    val hobbyInfoList: List<HobbyChipDomain>
)

data class HobbyChipDomain(
    val hobbyId: Int,
    val hobbyName: String,
    val todayRecorded: Boolean
)
