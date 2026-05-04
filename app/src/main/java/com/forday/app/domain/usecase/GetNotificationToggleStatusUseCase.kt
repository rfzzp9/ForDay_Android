package com.forday.app.domain.usecase

import com.forday.app.domain.model.NotificationToggleStatusDomain
import com.forday.app.domain.repository.NotificationRepository
import javax.inject.Inject

class GetNotificationToggleStatusUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(): Result<NotificationToggleStatusDomain> =
        runCatching {
            notificationRepository.getNotificationToggleStatus()
        }
}
