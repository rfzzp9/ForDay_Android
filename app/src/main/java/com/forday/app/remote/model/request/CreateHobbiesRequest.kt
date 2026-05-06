package com.forday.app.remote.model.request

import com.google.gson.annotations.SerializedName

data class CreateHobbiesRequest(
    @SerializedName("hobbyList")
    val hobbyList: List<CreateHobbyItemRequest>,
)

data class CreateHobbyItemRequest(
    @SerializedName("hobbyInfoId")
    val hobbyInfoId: Long?,
    @SerializedName("hobbyName")
    val hobbyName: String,
)
