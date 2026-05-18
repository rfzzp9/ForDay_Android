package com.forday.app.remote.model.response

import com.forday.app.data.model.DeleteHobbyDataEntity
import com.forday.app.data.model.DeleteHobbyEntity
import com.forday.app.remote.RemoteMapper
import com.google.gson.annotations.SerializedName

data class DeleteHobbyResponse(
    @SerializedName("status")
    val status: Int,
    @SerializedName("success")
    val isSuccess: Boolean,
    @SerializedName("data")
    val data: DeleteHobbyDataResponse,
) : RemoteMapper<DeleteHobbyEntity> {
    override fun toData(): DeleteHobbyEntity = DeleteHobbyEntity(
        status = status,
        isSuccess = isSuccess,
        data = data.toData(),
    )
}

data class DeleteHobbyDataResponse(
    @SerializedName("hobbyId")
    val hobbyId: Long?,
    @SerializedName("message")
    val message: String,
    @SerializedName("errorClassName")
    val errorClassName: String? = null,
) : RemoteMapper<DeleteHobbyDataEntity> {
    override fun toData(): DeleteHobbyDataEntity = DeleteHobbyDataEntity(
        hobbyId = hobbyId,
        message = message,
        errorClassName = errorClassName,
    )
}
