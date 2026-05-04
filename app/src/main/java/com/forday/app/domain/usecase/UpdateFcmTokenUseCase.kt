package com.forday.app.domain.usecase

import com.forday.app.domain.model.FcmTokenUpdateDomain
import com.forday.app.domain.repository.NotificationRepository
import javax.inject.Inject

class UpdateFcmTokenUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(): Result<FcmTokenUpdateDomain> =
        runCatching {
            notificationRepository.updateFcmToken()
        }
}
