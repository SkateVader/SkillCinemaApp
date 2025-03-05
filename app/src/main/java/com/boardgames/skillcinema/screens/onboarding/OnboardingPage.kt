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
        title = "Открывайте новые фильмы",
        description = "Используйте удобный поиск и подборки",
        imageRes = R.drawable.ic_onboarding1
    ),
    OnboardingPage(
        title = "Собирайте коллекции",
        description = "Добавляйте фильмы в любимые, создавайте свои подборки",
        imageRes = R.drawable.ic_onboarding2
    ),
    OnboardingPage(
        title = "Следите за новинками",
        description = "Получайте свежую информацию о премьерах",
        imageRes = R.drawable.ic_onboarding3
    )
)
