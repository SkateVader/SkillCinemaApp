package com.boardgames.skillcinema.screens.onboarding

import androidx.annotation.DrawableRes
import com.boardgames.skillcinema.R

data class OnboardingPage(
    val title: String,
    val description: String,
    @DrawableRes val imageRes: Int
)

val onboardingPages = listOf(
    OnboardingPage(
        title = "",
        description = "Узнавай \nо премьерах",
        imageRes = R.drawable.page1
    ),
    OnboardingPage(
        title = "",
        description = "Создавай \nколлекции",
        imageRes = R.drawable.page2
    ),
    OnboardingPage(
        title = "",
        description = "Делись \nс друзьями",
        imageRes = R.drawable.page3
    )
)
