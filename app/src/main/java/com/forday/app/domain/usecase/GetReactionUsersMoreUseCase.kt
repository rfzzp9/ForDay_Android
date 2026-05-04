package com.forday.app.domain.usecase

import com.forday.app.domain.repository.RoutineRepository
import javax.inject.Inject

class GetReactionUsersMoreUseCase @Inject constructor(
    private val routineRepository: RoutineRepository
) {
    suspend operator fun invoke(recordId: Int, type: String?, lastReactionId: Long, size: Int) =
        routineRepository.getReactionUsersMore(recordId, type, lastReactionId, size)
}
