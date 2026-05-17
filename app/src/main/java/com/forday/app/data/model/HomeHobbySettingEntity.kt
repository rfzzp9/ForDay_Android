package com.forday.app.data.model

import com.forday.app.data.DataMapper
import com.forday.app.domain.model.HomeHobbySettingDataDomain
import com.forday.app.domain.model.HomeHobbySettingDomain
import com.forday.app.domain.model.HomeHobbySettingItemDomain

data class HomeHobbySettingEntity(
    val status: Int,
    val isSuccess: Boolean,
    val data: HomeHobbySettingDataEntity,
) : DataMapper<HomeHobbySettingDomain> {
    override fun toDomain(): HomeHobbySettingDomain = HomeHobbySettingDomain(
        status = status,
        isSuccess = isSuccess,
        data = data.toDomain(),
    )
}

data class HomeHobbySettingDataEntity(
    val progressHobbyList: List<HomeHobbySettingItemEntity>,
    val hiddenHobbyList: List<HomeHobbySettingItemEntity>,
) : DataMapper<HomeHobbySettingDataDomain> {
    override fun toDomain(): HomeHobbySettingDataDomain = HomeHobbySettingDataDomain(
        progressHobbyList = progressHobbyList.map { it.toDomain() },
        hiddenHobbyList = hiddenHobbyList.map { it.toDomain() },
    )
}

data class HomeHobbySettingItemEntity(
    val hobbyId: Long,
    val hobbyName: String,
    val status: String,
    val imageIcon: String,
    val createdAt: String,
    val deletable: Boolean,
) : DataMapper<HomeHobbySettingItemDomain> {
    override fun toDomain(): HomeHobbySettingItemDomain = HomeHobbySettingItemDomain(
        hobbyId = hobbyId,
        hobbyName = hobbyName,
        status = status,
        imageIcon = imageIcon,
        createdAt = createdAt,
        deletable = deletable,
    )
}
