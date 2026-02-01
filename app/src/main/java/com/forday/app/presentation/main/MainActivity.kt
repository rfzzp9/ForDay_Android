package com.forday.app.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.forday.app.core.designsystem.theme.ForDayTheme
import com.forday.app.presentation.modifyhobby.screen.ModifyHobbyScreenRoot
import com.forday.app.presentation.onboarding.OnboardingViewModel
import com.forday.app.presentation.record.screen.RecordRoutineScreenRoot
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setKeepOnScreenCondition { false }
        super.onCreate(savedInstanceState)
        setContent {
//            RecordRoutineScreenRoot(onComplete = {})
            AppEntryPoint()

        }
    }
}