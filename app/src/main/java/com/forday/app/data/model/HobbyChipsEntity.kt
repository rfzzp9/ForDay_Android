package com.forday.app.data.model

import com.forday.app.data.DataMapper
import com.forday.app.domain.model.HobbyChipDomain
import com.forday.app.domain.model.HobbyChipsDomain

data class HobbyChipsEntity(
    val hobbyInfoList: List<HobbyChipEntity>
) : DataMapper<HobbyChipsDomain> {
    override fun toDomain(): HobbyChipsDomain = HobbyChipsDomain(
        hobbyInfoList = hobbyInfoList.map { it.toDomain() }
    )
}

data class HobbyChipEntity(
    val hobbyId: Int,
    val hobbyName: String,
    val todayRecorded: Boolean
) : DataMapper<HobbyChipDomain> {
    override fun toDomain(): HobbyChipDomain = HobbyChipDomain(
        hobbyId = hobbyId,
        hobbyName = hobbyName,
        todayRecorded = todayRecorded
    )
}
