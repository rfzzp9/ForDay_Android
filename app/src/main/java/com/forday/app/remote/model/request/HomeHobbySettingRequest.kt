package com.forday.app.remote.model.request

data class HomeHobbySettingRequest(
    val progressHobbyList: List<HomeHobbySettingProgressHobbyRequest>,
    val hiddenHobbyList: List<HomeHobbySettingHiddenHobbyRequest>,
)

data class HomeHobbySettingProgressHobbyRequest(
    val hobbyId: Long,
    val sequence: Int,
)

data class HomeHobbySettingHiddenHobbyRequest(
    val hobbyId: Long,
    val sequence: Int,
)
