package com.forday.app.data.model

import com.forday.app.data.DataMapper
import com.forday.app.domain.model.DeleteHobbyDataDomain
import com.forday.app.domain.model.DeleteHobbyDomain

data class DeleteHobbyEntity(
    val status: Int,
    val isSuccess: Boolean,
    val data: DeleteHobbyDataEntity,
) : DataMapper<DeleteHobbyDomain> {
    override fun toDomain(): DeleteHobbyDomain = DeleteHobbyDomain(
        status = status,
        isSuccess = isSuccess,
        data = data.toDomain(),
    )
}

data class DeleteHobbyDataEntity(
    val hobbyId: Long?,
    val message: String,
    val errorClassName: String?,
) : DataMapper<DeleteHobbyDataDomain> {
    override fun toDomain(): DeleteHobbyDataDomain = DeleteHobbyDataDomain(
        hobbyId = hobbyId,
        message = message,
        errorClassName = errorClassName,
    )
}
