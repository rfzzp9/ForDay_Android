package com.forday.app.domain.model

data class HomeHobbySettingDomain(
    val status: Int,
    val isSuccess: Boolean,
    val data: HomeHobbySettingDataDomain,
)

data class HomeHobbySettingDataDomain(
    val progressHobbyList: List<HomeHobbySettingItemDomain>,
    val hiddenHobbyList: List<HomeHobbySettingItemDomain>,
)

data class HomeHobbySettingItemDomain(
    val hobbyId: Long,
    val hobbyName: String,
    val status: String,
    val imageIcon: String,
    val createdAt: String,
    val deletable: Boolean,
)

data class HomeHobbySettingProgressHobbyRequestDomain(
    val hobbyId: Long,
    val sequence: Int,
)

data class HomeHobbySettingHiddenHobbyRequestDomain(
    val hobbyId: Long,
    val sequence: Int,
)
