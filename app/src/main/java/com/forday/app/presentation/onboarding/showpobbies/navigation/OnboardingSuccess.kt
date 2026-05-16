package com.forday.app.presentation.onboarding.showpobbies.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class OnboardingSuccess(
    val goHomeAfterShowPobbies: Boolean = false,
) : NavKey // 온보딩 완료 화면
