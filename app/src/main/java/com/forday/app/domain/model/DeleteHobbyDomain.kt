package com.forday.app.domain.model

data class DeleteHobbyDomain(
    val status: Int,
    val isSuccess: Boolean,
    val data: DeleteHobbyDataDomain,
)

data class DeleteHobbyDataDomain(
    val hobbyId: Long?,
    val message: String,
    val errorClassName: String?,
)
