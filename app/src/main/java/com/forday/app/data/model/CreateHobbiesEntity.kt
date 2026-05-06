package com.forday.app.data.model

import com.forday.app.data.DataMapper
import com.forday.app.domain.model.CreateHobbiesDataDomain
import com.forday.app.domain.model.CreateHobbiesDomain
import com.forday.app.domain.model.CreatedHobbyInfoDomain

data class CreateHobbiesEntity(
    val status: Int,
    val isSuccess: Boolean,
    val data: CreateHobbiesDataEntity,
) : DataMapper<CreateHobbiesDomain> {
    override fun toDomain(): CreateHobbiesDomain {
        return CreateHobbiesDomain(
            status = status,
            isSuccess = isSuccess,
            data = data.toDomain(),
        )
    }
}

data class CreateHobbiesDataEntity(
    val message: String,
    val createdHobbyCount: Int,
    val createdHobbyInfoList: List<CreatedHobbyInfoEntity>,
) : DataMapper<CreateHobbiesDataDomain> {
    override fun toDomain(): CreateHobbiesDataDomain {
        return CreateHobbiesDataDomain(
            message = message,
            createdHobbyCount = createdHobbyCount,
            createdHobbyInfoList = createdHobbyInfoList.map { it.toDomain() },
        )
    }
}

data class CreatedHobbyInfoEntity(
    val hobbyId: Long,
    val hobbyInfoId: Long?,
    val hobbyName: String,
) : DataMapper<CreatedHobbyInfoDomain> {
    override fun toDomain(): CreatedHobbyInfoDomain {
        return CreatedHobbyInfoDomain(
            hobbyId = hobbyId,
            hobbyInfoId = hobbyInfoId,
            hobbyName = hobbyName,
        )
    }
}
