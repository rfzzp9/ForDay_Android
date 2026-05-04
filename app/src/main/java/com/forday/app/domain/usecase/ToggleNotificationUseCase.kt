package com.forday.app.domain.usecase

import com.forday.app.domain.model.NotificationToggleDomain
import com.forday.app.domain.repository.NotificationRepository
import javax.inject.Inject

class ToggleNotificationUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(active: Boolean, toggleType: String = "APP"): Result<NotificationToggleDomain> =
        runCatching {
            notificationRepository.toggleNotification(active, toggleType)
        }
}
