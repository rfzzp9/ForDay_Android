package com.forday.app.domain.model

data class CreateHobbiesDomain(
    val status: Int,
    val isSuccess: Boolean,
    val data: CreateHobbiesDataDomain,
)

data class CreateHobbiesDataDomain(
    val message: String,
    val createdHobbyCount: Int,
    val createdHobbyInfoList: List<CreatedHobbyInfoDomain>,
)

data class CreatedHobbyInfoDomain(
    val hobbyId: Long,
    val hobbyInfoId: Long?,
    val hobbyName: String,
)

data class CreateHobbyItemDomain(
    val hobbyInfoId: Long?,
    val hobbyName: String,
)
