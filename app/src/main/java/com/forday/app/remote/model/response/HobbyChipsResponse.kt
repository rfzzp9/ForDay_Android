package com.forday.app.remote.model.response

import com.forday.app.data.model.HobbyChipEntity
import com.forday.app.data.model.HobbyChipsEntity
import com.forday.app.remote.RemoteMapper
import com.google.gson.annotations.SerializedName

data class HobbyChipsResponse(
    @SerializedName("status") val status: Int?,
    @SerializedName("success") val isSuccess: Boolean?,
    @SerializedName("data") val data: HobbyChipsDataResponse?
) : RemoteMapper<HobbyChipsEntity> {
    override fun toData(): HobbyChipsEntity = HobbyChipsEntity(
        hobbyInfoList = data?.hobbyInfoList?.map { it.toData() } ?: emptyList()
    )
}

data class HobbyChipsDataResponse(
    @SerializedName("hobbyInfoList") val hobbyInfoList: List<HobbyChipResponse>?
)

data class HobbyChipResponse(
    @SerializedName("hobbyId") val hobbyId: Int?,
    @SerializedName("hobbyName") val hobbyName: String?,
    @SerializedName("todayRecorded") val todayRecorded: Boolean?
) {
    fun toData(): HobbyChipEntity = HobbyChipEntity(
        hobbyId = hobbyId ?: 0,
        hobbyName = hobbyName.orEmpty(),
        todayRecorded = todayRecorded ?: false
    )
}
