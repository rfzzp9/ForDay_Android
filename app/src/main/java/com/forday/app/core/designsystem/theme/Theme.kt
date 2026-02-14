package com.forday.app.core.designsystem.theme

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat

@Composable
fun ForDayTheme(
    content: @Composable () -> Unit,
) {
    if (!LocalInspectionMode.current) {
        val view = LocalView.current

        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            val insetsController = WindowCompat.getInsetsController(window, view)

            WindowCompat.setDecorFitsSystemWindows(window, true)

            window.statusBarColor = FordayColor.White.toArgb()
            window.navigationBarColor = FordayColor.White.toArgb()

            insetsController.isAppearanceLightStatusBars = true
            insetsController.isAppearanceLightNavigationBars = true
        }
    }

    CompositionLocalProvider(
        LocalAppTypography provides LocalTypography,
        LocalAppColor provides FordayColor,
        LocalAppGradients provides ForDayGradients,
        content = content,
    )
}

object ForDayTheme {
    val typography: FordayTypography
        @Composable
        get() = LocalAppTypography.current

    val color: FordayColor
        @Composable
        get() = LocalAppColor.current

    val gradients: ForDayGradients
        @Composable
        get() = LocalAppGradients.current
}