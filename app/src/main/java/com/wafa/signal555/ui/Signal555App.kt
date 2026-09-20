package com.wafa.signal555.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.wafa.signal555.ui.screens.AskScreen
import com.wafa.signal555.ui.screens.CameraAssistScreen
import com.wafa.signal555.ui.screens.CheckScreen
import com.wafa.signal555.ui.screens.DocumentAiScreen
import com.wafa.signal555.ui.screens.HistoryScreen
import com.wafa.signal555.ui.screens.HomeScreen
import com.wafa.signal555.ui.screens.LinkCheckScreen
import com.wafa.signal555.ui.screens.NextActionScreen
import com.wafa.signal555.ui.screens.ProfileScreen
import com.wafa.signal555.ui.screens.ScreenshotCheckScreen
import com.wafa.signal555.ui.screens.SplashScreen

internal enum class AppScreen {
    Splash,
    Home,
    Check,
    Ask,
    History,
    Profile,
    ScreenshotCheck,
    LinkCheck,
    CameraAssist,
    DocumentAi,
    NextAction
}

@Composable
fun Signal555App() {
    var screen by rememberSaveable { mutableStateOf(AppScreen.Splash) }
    val backStack = remember { mutableListOf<AppScreen>() }

    fun goTopLevel(destination: AppScreen) {
        backStack.clear()
        screen = destination
    }

    fun openDetail(destination: AppScreen) {
        if (screen != destination) {
            backStack.add(screen)
            screen = destination
        }
    }

    fun goBack() {
        screen = if (backStack.isNotEmpty()) {
            backStack.removeAt(backStack.lastIndex)
        } else {
            AppScreen.Home
        }
    }

    val goHome = { goTopLevel(AppScreen.Home) }
    val goCheck = { goTopLevel(AppScreen.Check) }
    val goAsk = { goTopLevel(AppScreen.Ask) }
    val goHistory = { goTopLevel(AppScreen.History) }
    val goProfile = { goTopLevel(AppScreen.Profile) }

    when (screen) {
        AppScreen.Splash -> SplashScreen(onContinue = goHome)

        AppScreen.Home -> HomeScreen(
            onScreenshot = { openDetail(AppScreen.ScreenshotCheck) },
            onLink = { openDetail(AppScreen.LinkCheck) },
            onCamera = { openDetail(AppScreen.CameraAssist) },
            onDocument = { openDetail(AppScreen.DocumentAi) },
            onCheck = goCheck,
            onAsk = goAsk,
            onHistory = goHistory,
            onProfile = goProfile
        )

        AppScreen.Check -> CheckScreen(
            onScreenshot = { openDetail(AppScreen.ScreenshotCheck) },
            onLink = { openDetail(AppScreen.LinkCheck) },
            onCamera = { openDetail(AppScreen.CameraAssist) },
            onDocument = { openDetail(AppScreen.DocumentAi) },
            onHome = goHome,
            onAsk = goAsk,
            onHistory = goHistory,
            onProfile = goProfile
        )

        AppScreen.Ask -> AskScreen(
            onHome = goHome,
            onCheck = goCheck,
            onHistory = goHistory,
            onProfile = goProfile
        )

        AppScreen.History -> HistoryScreen(
            onHome = goHome,
            onCheck = goCheck,
            onAsk = goAsk,
            onProfile = goProfile
        )

        AppScreen.Profile -> ProfileScreen(
            onHome = goHome,
            onCheck = goCheck,
            onAsk = goAsk,
            onHistory = goHistory
        )

        AppScreen.ScreenshotCheck -> ScreenshotCheckScreen(
            onBack = ::goBack,
            onAsk = goAsk,
            onNextAction = { openDetail(AppScreen.NextAction) }
        )

        AppScreen.LinkCheck -> LinkCheckScreen(
            onBack = ::goBack,
            onAsk = goAsk,
            onNextAction = { openDetail(AppScreen.NextAction) }
        )

        AppScreen.CameraAssist -> CameraAssistScreen(
            onBack = ::goBack,
            onAsk = goAsk
        )

        AppScreen.DocumentAi -> DocumentAiScreen(
            onBack = ::goBack,
            onNextAction = { openDetail(AppScreen.NextAction) }
        )

        AppScreen.NextAction -> NextActionScreen(
            onBack = ::goBack,
            onDone = goCheck
        )
    }
}
