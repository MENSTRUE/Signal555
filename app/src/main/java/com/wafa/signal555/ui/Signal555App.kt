package com.wafa.signal555.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.wafa.signal555.ui.screens.AskScreen
import com.wafa.signal555.ui.screens.CameraAssistScreen
import com.wafa.signal555.ui.screens.DocumentAiScreen
import com.wafa.signal555.ui.screens.HistoryScreen
import com.wafa.signal555.ui.screens.HomeScreen
import com.wafa.signal555.ui.screens.NextActionScreen
import com.wafa.signal555.ui.screens.ProfileScreen
import com.wafa.signal555.ui.screens.ScreenshotCheckScreen
import com.wafa.signal555.ui.screens.SplashScreen

internal enum class AppScreen {
    Splash,
    Home,
    ScreenshotCheck,
    CameraAssist,
    DocumentAi,
    NextAction,
    Ask,
    History,
    Profile
}

@Composable
fun Signal555App() {
    var screen by rememberSaveable { mutableStateOf(AppScreen.Splash) }

    val goHome = { screen = AppScreen.Home }
    val goAsk = { screen = AppScreen.Ask }
    val goHistory = { screen = AppScreen.History }
    val goProfile = { screen = AppScreen.Profile }

    when (screen) {
        AppScreen.Splash -> SplashScreen(onContinue = goHome)
        AppScreen.Home -> HomeScreen(
            onScreenshot = { screen = AppScreen.ScreenshotCheck },
            onCamera = { screen = AppScreen.CameraAssist },
            onDocument = { screen = AppScreen.DocumentAi },
            onAsk = goAsk,
            onHistory = goHistory,
            onProfile = goProfile
        )
        AppScreen.ScreenshotCheck -> ScreenshotCheckScreen(
            onBack = goHome,
            onAsk = goAsk,
            onNextAction = { screen = AppScreen.NextAction }
        )
        AppScreen.CameraAssist -> CameraAssistScreen(onBack = goHome, onAsk = goAsk)
        AppScreen.DocumentAi -> DocumentAiScreen(
            onBack = goHome,
            onNextAction = { screen = AppScreen.NextAction }
        )
        AppScreen.NextAction -> NextActionScreen(onBack = goHome, onDone = goHome)
        AppScreen.Ask -> AskScreen(onBack = goHome)
        AppScreen.History -> HistoryScreen(onBack = goHome)
        AppScreen.Profile -> ProfileScreen(onBack = goHome)
    }
}
