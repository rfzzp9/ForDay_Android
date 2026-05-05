package com.forday.app.presentation.onboarding.experiment

import com.forday.app.core.firebase.ONBOARDING_VARIANT_NEW
import com.forday.app.core.firebase.ONBOARDING_VARIANT_OLD

enum class OnboardingAbVariant(val remoteValue: String) {
    OLD(ONBOARDING_VARIANT_OLD),
    NEW(ONBOARDING_VARIANT_NEW);

    companion object {
        fun fromRemoteValue(value: String?): OnboardingAbVariant {
            return values().firstOrNull { it.remoteValue == value } ?: OLD
        }
    }
}
