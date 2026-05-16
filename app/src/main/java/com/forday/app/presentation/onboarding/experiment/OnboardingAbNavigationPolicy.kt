package com.forday.app.presentation.onboarding.experiment

import androidx.navigation3.runtime.NavKey
import com.forday.app.presentation.home.navigation.Home
import com.forday.app.presentation.onboarding.experiment.navigation.MyHobbySelect
import com.forday.app.presentation.onboarding.hobbyselect.navigation.SelectHobby
import com.forday.app.presentation.onboarding.nicknameinput.navigation.InputNickname

object OnboardingAbNavigationPolicy {
    fun routeAfterIncompleteOnboarding(variant: OnboardingAbVariant): NavKey =
        when (variant) {
            OnboardingAbVariant.OLD -> SelectHobby
            OnboardingAbVariant.NEW -> InputNickname
        }

    fun routeAfterTermsConsent(variant: OnboardingAbVariant): NavKey =
        routeAfterIncompleteOnboarding(variant)

    fun routeAfterNicknameRegistration(
        variant: OnboardingAbVariant,
        userName: String,
    ): NavKey =
        when (variant) {
            OnboardingAbVariant.OLD -> Home
            OnboardingAbVariant.NEW -> MyHobbySelect(userName = userName)
        }
}
