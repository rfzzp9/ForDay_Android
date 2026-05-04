package com.forday.app.presentation.modifyhobby.navigation

import androidx.compose.ui.graphics.Color
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.forday.app.presentation.main.Navigator
import com.forday.app.presentation.main.nonTabEntry
import com.forday.app.presentation.modifyhobby.screen.ModifyHobbyRoute
import com.forday.app.presentation.onboarding.hobbyselect.navigation.SelectHobbyFromModify
import com.forday.app.presentation.onboarding.frequencyselect.navigation.SelectPerWeek
import com.forday.app.presentation.onboarding.periodselect.navigation.SelectPeriod
import com.forday.app.presentation.onboarding.timeselect.ScreenMode
import com.forday.app.presentation.onboarding.timeselect.navigation.SelectPerTime

fun EntryProviderScope<NavKey>.modifyHobbyNavEntry(
    navigator: Navigator,
) {
    nonTabEntry<ModifyHobby>(backgroundColor = Color(0xFFF9F9F9)) {
        ModifyHobbyRoute(
            onAddHobby = { navigator.navigate(SelectHobbyFromModify) },
            onChangeDuration = { params -> navigator.navigate(SelectPerTime(params = params, mode = ScreenMode.DEFAULT)) },
            onChangeFrequency = { params -> navigator.navigate(SelectPerWeek(params = params, mode = ScreenMode.DEFAULT)) },
            onChangeJourneyDays = { params -> navigator.navigate(SelectPeriod(params = params, mode = ScreenMode.DEFAULT)) },
            onBack = { navigator.goBack() },
        )
    }
}
