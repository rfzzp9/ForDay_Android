package com.forday.app.domain.usecase

import com.forday.app.domain.model.GetNotificationsDomain
import com.forday.app.domain.repository.NotificationRepository
import javax.inject.Inject

class GetNotificationsUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(
        filterType: String = "ALL",
        lastNotificationId: Int? = null,
        pageSize: Int = 20
    ): Result<GetNotificationsDomain> = runCatching {
        notificationRepository.getNotifications(filterType, lastNotificationId, pageSize)
    }
}
