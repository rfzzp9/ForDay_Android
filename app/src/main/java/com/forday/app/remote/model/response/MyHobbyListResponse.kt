package com.forday.app.remote.model.response

import com.forday.app.data.model.MyHobbyListDataEntity
import com.forday.app.data.model.MyHobbyListEntity
import com.forday.app.data.model.MyHobbyListItemEntity
import com.forday.app.remote.RemoteMapper
import com.google.gson.annotations.SerializedName
import kotlin.collections.map

data class MyHobbyListResponse(
    @SerializedName("status") val status: Int,
    @SerializedName("success") val isSuccess: Boolean,
    @SerializedName("data") val data: MyHobbyListData
) : RemoteMapper<MyHobbyListEntity> {
    override fun toData(): MyHobbyListEntity = MyHobbyListEntity(
        status = status,
        isSuccess = isSuccess,
        data = data.toData()
    )
}

data class MyHobbyListData(
    @SerializedName("currentHobbyStatus") val currentHobbyStatus: String,
    @SerializedName("inProgressHobbyCount") val inProgressHobbyCount: Int,
    @SerializedName("archivedHobbyCount") val archivedHobbyCount: Int,
    @SerializedName("hobbies") val hobbies: List<MyHobbyListItem>
) : RemoteMapper<MyHobbyListDataEntity> {
    override fun toData(): MyHobbyListDataEntity = MyHobbyListDataEntity(
        currentHobbyStatus = currentHobbyStatus,
        inProgressHobbyCount = inProgressHobbyCount,
        archivedHobbyCount = archivedHobbyCount,
        hobbies = hobbies.map { it.toData() } // 빈 리스트인 경우 자동으로 빈 리스트 반환
    )
}

data class MyHobbyListItem(
    @SerializedName("hobbyId") val hobbyId: Int,
    @SerializedName("hobbyName") val hobbyName: String,
    @SerializedName("hobbyTimeMinutes") val hobbyTimeMinutes: Int,
    @SerializedName("executionCount") val executionCount: Int,
    @SerializedName("goalDays") val goalDays: Int
) : RemoteMapper<MyHobbyListItemEntity> {
    override fun toData(): MyHobbyListItemEntity = MyHobbyListItemEntity(
        hobbyId = hobbyId,
        hobbyName = hobbyName,
        hobbyTimeMinutes = hobbyTimeMinutes,
        executionCount = executionCount,
        goalDays = goalDays
    )
}