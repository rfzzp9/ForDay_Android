package com.forday.app.remote.model.response

import com.forday.app.data.model.CreateHobbiesDataEntity
import com.forday.app.data.model.CreateHobbiesEntity
import com.forday.app.data.model.CreatedHobbyInfoEntity
import com.forday.app.remote.RemoteMapper
import com.google.gson.annotations.SerializedName

data class CreateHobbiesResponse(
    @SerializedName("status")
    val status: Int,
    @SerializedName("success")
    val isSuccess: Boolean,
    @SerializedName("data")
    val data: CreateHobbiesDataResponse,
) : RemoteMapper<CreateHobbiesEntity> {
    override fun toData(): CreateHobbiesEntity {
        return CreateHobbiesEntity(
            status = status,
            isSuccess = isSuccess,
            data = data.toData(),
        )
    }
}

data class CreateHobbiesDataResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("createdHobbyCount")
    val createdHobbyCount: Int,
    @SerializedName("createdHobbyInfoList")
    val createdHobbyInfoList: List<CreatedHobbyInfoResponse>,
) : RemoteMapper<CreateHobbiesDataEntity> {
    override fun toData(): CreateHobbiesDataEntity {
        return CreateHobbiesDataEntity(
            message = message,
            createdHobbyCount = createdHobbyCount,
            createdHobbyInfoList = createdHobbyInfoList.map { it.toData() },
        )
    }
}

data class CreatedHobbyInfoResponse(
    @SerializedName("hobbyId")
    val hobbyId: Long,
    @SerializedName("hobbyInfoId")
    val hobbyInfoId: Long?,
    @SerializedName("hobbyName")
    val hobbyName: String,
) : RemoteMapper<CreatedHobbyInfoEntity> {
    override fun toData(): CreatedHobbyInfoEntity {
        return CreatedHobbyInfoEntity(
            hobbyId = hobbyId,
            hobbyInfoId = hobbyInfoId,
            hobbyName = hobbyName,
        )
    }
}
