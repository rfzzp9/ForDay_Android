package com.forday.app.remote.model.response

import com.forday.app.data.model.HomeHobbySettingDataEntity
import com.forday.app.data.model.HomeHobbySettingEntity
import com.forday.app.data.model.HomeHobbySettingItemEntity
import com.forday.app.remote.RemoteMapper
import com.google.gson.annotations.SerializedName

data class HomeHobbySettingResponse(
    @SerializedName("status")
    val status: Int,
    @SerializedName("success")
    val isSuccess: Boolean,
    @SerializedName("data")
    val data: HomeHobbySettingDataResponse,
) : RemoteMapper<HomeHobbySettingEntity> {
    override fun toData(): HomeHobbySettingEntity = HomeHobbySettingEntity(
        status = status,
        isSuccess = isSuccess,
        data = data.toData(),
    )
}

data class HomeHobbySettingDataResponse(
    @SerializedName("progressHobbyList")
    val progressHobbyList: List<HomeHobbySettingItemResponse>,
    @SerializedName("hiddenHobbyList")
    val hiddenHobbyList: List<HomeHobbySettingItemResponse>,
) : RemoteMapper<HomeHobbySettingDataEntity> {
    override fun toData(): HomeHobbySettingDataEntity = HomeHobbySettingDataEntity(
        progressHobbyList = progressHobbyList.map { it.toData() },
        hiddenHobbyList = hiddenHobbyList.map { it.toData() },
    )
}

data class HomeHobbySettingItemResponse(
    @SerializedName("hobbyId")
    val hobbyId: Long,
    @SerializedName("hobbyName")
    val hobbyName: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("imageIcon")
    val imageIcon: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("deletable")
    val deletable: Boolean?,
) : RemoteMapper<HomeHobbySettingItemEntity> {
    override fun toData(): HomeHobbySettingItemEntity = HomeHobbySettingItemEntity(
        hobbyId = hobbyId,
        hobbyName = hobbyName,
        status = status,
        imageIcon = imageIcon,
        createdAt = createdAt,
        deletable = deletable ?: true,
    )
}
