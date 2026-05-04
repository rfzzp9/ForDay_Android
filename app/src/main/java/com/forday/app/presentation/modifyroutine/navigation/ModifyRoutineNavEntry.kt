package com.forday.app.presentation.modifyroutine.navigation

import androidx.compose.ui.graphics.Color
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.forday.app.presentation.inputhobbyroutines.navigation.InputRoutine
import com.forday.app.presentation.main.Navigator
import com.forday.app.presentation.main.nonTabEntry
import com.forday.app.presentation.modifyroutine.screen.ModifyRoutineRoute

fun EntryProviderScope<NavKey>.modifyRoutineNavEntry(
    navigator: Navigator,
) {
    nonTabEntry<ModifyRoutine>(backgroundColor = Color(0xFFF9F9F9)) { backStackEntry ->
        val hobbyId = backStackEntry.hobbyId
        val hobbyName = backStackEntry.hobbyName
        ModifyRoutineRoute(
            hobbyId = hobbyId,
            onBack = { navigator.goBack() },
            onAddRoutine = { navigator.navigate(InputRoutine(hobbyId, hobbyName = hobbyName)) },
            onEditRoutine = {},
        )
    }
}
