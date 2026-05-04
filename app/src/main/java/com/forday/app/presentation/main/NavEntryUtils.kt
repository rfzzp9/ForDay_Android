package com.forday.app.presentation.main

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.forday.app.core.designsystem.component.layout.ForDayScreenWrapper
import com.forday.app.core.designsystem.component.navigationbar.BottomBar

internal fun Context.findActivity(): Activity? {
    var current = this
    while (current is ContextWrapper) {
        if (current is Activity) return current
        current = current.baseContext
    }
    return null
}

internal inline fun <reified T : NavKey> EntryProviderScope<NavKey>.nonTabEntry(
    backgroundColor: Color = Color.White,
    noinline content: @Composable (T) -> Unit
) {
    entry<T> { key ->
        ForDayScreenWrapper(backgroundColor = backgroundColor) {
            content(key)
        }
    }
}

@Composable
internal fun MainTabScaffold(
    navigationState: MainNavigationState,
    onTabSelected: (com.forday.app.core.designsystem.component.navigationbar.BottomBarTab) -> Unit,
    onRecordClick: () -> Unit,
    backgroundColor: Color = Color.White,
    content: @Composable (PaddingValues) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize().background(backgroundColor)) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets.statusBars,
            bottomBar = {
                BottomBar(
                    modifier = Modifier,
                    selectedTab = navigationState.topLevelRoute.toBottomBarTab(),
                    onTabSelected = onTabSelected,
                    onRecordClick = onRecordClick,
                )
            },
            content = content,
        )
    }
}
