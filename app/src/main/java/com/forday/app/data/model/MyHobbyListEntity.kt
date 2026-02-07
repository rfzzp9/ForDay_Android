package com.forday.app.data.model

import com.forday.app.data.DataMapper
import com.forday.app.domain.model.*
import kotlin.collections.map

data class MyHobbyListEntity(
    val status: Int,
    val isSuccess: Boolean,
    val data: MyHobbyListDataEntity
) : DataMapper<MyHobbyListDomain> {
    override fun toDomain(): MyHobbyListDomain = MyHobbyListDomain(
        status = status,
        isSuccess = isSuccess,
        data = data.toDomain()
    )
}

data class MyHobbyListDataEntity(
    val currentHobbyStatus: String,
    val inProgressHobbyCount: Int,
    val archivedHobbyCount: Int,
    val hobbies: List<MyHobbyListItemEntity> = emptyList()
) : DataMapper<MyHobbyListDataDomain> {
    override fun toDomain(): MyHobbyListDataDomain = MyHobbyListDataDomain(
        currentHobbyStatus = currentHobbyStatus,
        inProgressHobbyCount = inProgressHobbyCount,
        archivedHobbyCount = archivedHobbyCount,
        hobbies = hobbies.map { it.toDomain() }
    )
}

data class MyHobbyListItemEntity(
    val hobbyId: Int,
    val hobbyName: String,
    val hobbyTimeMinutes: Int,
    val executionCount: Int,
    val goalDays: Int
) : DataMapper<MyHobbyListItemDomain> {
    override fun toDomain(): MyHobbyListItemDomain = MyHobbyListItemDomain(
        hobbyId, hobbyName, hobbyTimeMinutes, executionCount, goalDays
    )
}