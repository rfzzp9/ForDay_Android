package com.forday.app.domain.usecase

import com.forday.app.domain.repository.Repository
import javax.inject.Inject

class GetExampleDataUseCase @Inject constructor(
    private val repository: Repository
) {
    suspend operator fun invoke() = repository.getExampleData()
}