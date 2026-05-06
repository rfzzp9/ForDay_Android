package com.forday.app.domain.usecase

import com.forday.app.domain.model.CreateHobbyItemDomain
import com.forday.app.domain.repository.HobbyRepository
import javax.inject.Inject

class CreateHobbiesUseCase @Inject constructor(
    private val hobbyRepository: HobbyRepository,
) {
    suspend operator fun invoke(hobbyList: List<CreateHobbyItemDomain>) =
        hobbyRepository.createHobbies(hobbyList)
}
