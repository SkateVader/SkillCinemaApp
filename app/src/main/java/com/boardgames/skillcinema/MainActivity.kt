package com.boardgames.skillcinema

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.boardgames.skillcinema.navigation.NavGraph
import com.boardgames.skillcinema.ui.theme.SkillCinemaTheme
import com.boardgames.skillcinema.screens.onboarding.OnboardingViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SkillCinemaTheme {
                val onboardingViewModel: OnboardingViewModel = hiltViewModel()
                val isOnboardingCompleted by onboardingViewModel.isOnboardingCompleted.collectAsState(initial = false)
                val startDestination = if (isOnboardingCompleted) "home" else "onboarding"
                NavGraph(startDestination = startDestination)
            }
        }
    }
}
