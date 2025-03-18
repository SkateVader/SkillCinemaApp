package com.boardgames.skillcinema

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.boardgames.skillcinema.navigation.NavGraph
import com.boardgames.skillcinema.screens.home.OnboardingLoadingPageUI
import com.boardgames.skillcinema.screens.onboarding.OnboardingViewModel
import com.boardgames.skillcinema.ui.theme.SkillCinemaTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SkillCinemaTheme {
                val onboardingViewModel: OnboardingViewModel = hiltViewModel()
                val isOnboardingCompleted
                by onboardingViewModel.isOnboardingCompleted.collectAsState(initial = null)

                when (isOnboardingCompleted) {
                    null -> {
                        // Если состояние ещё не загружено – показываем экран с индикатором
                        val navController = rememberNavController()
                        OnboardingLoadingPageUI(navController = navController)
                    }
                    true -> {
                        // Если онбординг завершён – сразу запускаем onboarding_loading, который переходит на home по isDataLoaded
                        NavGraph(startDestination = "onboarding_loading")
                    }
                    false -> {
                        // Если онбординг ещё не пройден – сначала показываем индикатор 3 секунды, затем запускаем onboarding
                        var delayCompleted by remember { mutableStateOf(false) }
                        LaunchedEffect(Unit) {
                            delay(1000)
                            delayCompleted = true
                        }
                        if (!delayCompleted) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        } else {
                            NavGraph(startDestination = "onboarding")
                        }
                    }
                }
            }
        }
    }
}
