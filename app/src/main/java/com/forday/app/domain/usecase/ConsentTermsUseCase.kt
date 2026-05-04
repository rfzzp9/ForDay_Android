package com.forday.app.domain.usecase

import com.forday.app.domain.repository.AuthRepository
import javax.inject.Inject

class ConsentTermsUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        serviceConsent: Boolean,
        ageOver14Consent: Boolean,
        privateConsent: Boolean,
        recordPushConsent: Boolean
    ) = authRepository.consentTerms(
        serviceConsent = serviceConsent,
        ageOver14Consent = ageOver14Consent,
        privateConsent = privateConsent,
        recordPushConsent = recordPushConsent
    ).getOrThrow()
}
