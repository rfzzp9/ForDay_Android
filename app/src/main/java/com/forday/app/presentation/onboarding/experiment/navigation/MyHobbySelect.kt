package com.forday.app.presentation.onboarding.experiment.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class MyHobbySelect(
    val userName: String,
) : NavKey
