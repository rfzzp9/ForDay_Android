package com.forday.app.domain.usecase

import com.forday.app.domain.repository.AuthRepository
import javax.inject.Inject

class SaveIsTermsAgreementRequiredUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(isTermsAgreementRequired: Boolean) =
        authRepository.saveIsTermsAgreementRequired(isTermsAgreementRequired)
}
