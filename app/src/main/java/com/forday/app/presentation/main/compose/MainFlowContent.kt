package com.forday.app.presentation.main.compose

import android.widget.Toast as AndroidToast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.forday.app.core.designsystem.theme.ForDayTheme
import com.forday.app.core.designsystem.toast.ErrorToast
import com.forday.app.presentation.home.navigation.Home
import com.forday.app.presentation.main.MainNavigationState
import com.forday.app.presentation.main.Navigator
import com.forday.app.presentation.main.TOP_LEVEL_DESTINATIONS
import com.forday.app.presentation.main.findActivity
import com.forday.app.presentation.main.toEntries
import kotlinx.coroutines.delay
import timber.log.Timber

@Composable
internal fun MainFlowContent(
    initialRoute: NavKey,
    navigationState: MainNavigationState,
    navigator: Navigator,
    entryProvider: (NavKey) -> NavEntry<NavKey>,
    toastMessage: String?,
    onToastMessageConsumed: () -> Unit,
) {
    ForDayTheme {
        MainNavigationLogEffect(navigationState)
        MainBackHandler(
            navigationState = navigationState,
            navigator = navigator,
        )

        Box(modifier = Modifier.fillMaxSize()) {
            Timber.e("@@@@@@@@@@@@@@@@@route: $initialRoute")
            NavDisplay(
                entries = navigationState.toEntries(entryProvider),
                onBack = { navigator.goBack() },
                modifier = Modifier.fillMaxSize(),
            )

            MainToastHost(
                navigationState = navigationState,
                toastMessage = toastMessage,
                onToastMessageConsumed = onToastMessageConsumed,
            )
        }
    }
}

@Composable
private fun MainNavigationLogEffect(
    navigationState: MainNavigationState,
) {
    LaunchedEffect(navigationState.changeId) {
        val activeKeys = navigationState.stacksInUse
        val lastRoutes = activeKeys.associateWith { key ->
            navigationState.backStacks[key]?.lastOrNull()
        }
        Timber.e(
            "NavState(changeId=${navigationState.changeId}) startRoute=${navigationState.startRoute} topLevelRoute=${navigationState.topLevelRoute} stacksInUse=$activeKeys lastRoutes=$lastRoutes"
        )
    }
}

@Composable
private fun MainBackHandler(
    navigationState: MainNavigationState,
    navigator: Navigator,
) {
    val context = LocalContext.current
    var lastBackPressedAt by remember { mutableLongStateOf(0L) }
    var exitToast by remember { mutableStateOf<AndroidToast?>(null) }

    BackHandler(enabled = true) {
        val handled = navigator.handleBack()
        if (!handled) {
            if (navigationState.topLevelRoute in TOP_LEVEL_DESTINATIONS.keys && navigationState.topLevelRoute != Home) {
                navigator.navigate(Home)
                return@BackHandler
            }
            val now = System.currentTimeMillis()
            if (now - lastBackPressedAt <= 2_000L) {
                val activity = context.findActivity()
                exitToast?.view?.animate()?.cancel()
                val animator = exitToast?.view?.animate()?.alpha(0f)?.setDuration(200L)?.withEndAction {
                    exitToast?.cancel()
                    exitToast = null
                    activity?.finish()
                }
                if (animator == null) {
                    exitToast?.cancel()
                    exitToast = null
                    activity?.finish()
                } else {
                    animator.start()
                }
            } else {
                lastBackPressedAt = now
                exitToast?.view?.animate()?.cancel()
                exitToast?.cancel()
                exitToast = AndroidToast.makeText(context, "한 번 더 누르면 종료됩니다", AndroidToast.LENGTH_SHORT).also { toast ->
                    toast.view?.alpha = 0f
                    toast.show()
                    toast.view?.animate()?.alpha(1f)?.setDuration(1000L)?.start()
                }
            }
        }
    }
}

@Composable
private fun BoxScope.MainToastHost(
    navigationState: MainNavigationState,
    toastMessage: String?,
    onToastMessageConsumed: () -> Unit,
) {
    val isTabRootScreen = navigationState.topLevelRoute in TOP_LEVEL_DESTINATIONS.keys &&
            navigationState.backStacks[navigationState.topLevelRoute]?.lastOrNull() == navigationState.topLevelRoute
    val toastBottomPadding = if (isTabRootScreen) 72.dp else 20.dp

    AnimatedVisibility(
        visible = toastMessage != null,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .navigationBarsPadding()
            .padding(bottom = toastBottomPadding)
    ) {
        ErrorToast(message = toastMessage.orEmpty())
    }

    LaunchedEffect(toastMessage) {
        if (toastMessage != null) {
            delay(2000L)
            onToastMessageConsumed()
        }
    }
}
