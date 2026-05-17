package com.forday.app.domain.usecase

import com.forday.app.domain.model.HomeHobbySettingHiddenHobbyRequestDomain
import com.forday.app.domain.model.HomeHobbySettingProgressHobbyRequestDomain
import com.forday.app.domain.repository.HobbyRepository
import javax.inject.Inject

class UpdateHomeHobbySettingListUseCase @Inject constructor(
    private val hobbyRepository: HobbyRepository,
) {
    suspend operator fun invoke(
        progressHobbyList: List<HomeHobbySettingProgressHobbyRequestDomain>,
        hiddenHobbyList: List<HomeHobbySettingHiddenHobbyRequestDomain>,
    ) = hobbyRepository.updateHomeHobbySettingList(
        progressHobbyList = progressHobbyList,
        hiddenHobbyList = hiddenHobbyList,
    )
}
