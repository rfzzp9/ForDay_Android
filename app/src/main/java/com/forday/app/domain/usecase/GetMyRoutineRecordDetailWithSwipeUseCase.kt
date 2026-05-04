package com.forday.app.domain.usecase

import com.forday.app.domain.repository.RoutineRepository
import javax.inject.Inject

class GetMyRoutineRecordDetailWithSwipeUseCase @Inject constructor(
    private val routineRepository: RoutineRepository
) {
    suspend operator fun invoke(
        recordId: Int,
        context: String,
        userId: String?,
        keyword: String?,
        hobbyIds: List<Long>,
        notificationId: Long?
    ) = routineRepository.getMyRoutineRecordDetailWithSwipe(recordId, context, userId, keyword, hobbyIds, notificationId)
}
