package com.boardgames.skillcinema.screens.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import com.boardgames.skillcinema.R

@Composable
fun OnboardingScreen(navController: NavController) {
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val coroutineScope = rememberCoroutineScope()
    val viewModel: OnboardingViewModel = hiltViewModel()

    Column(modifier = Modifier.fillMaxSize()) {
        // HorizontalPager занимает всю оставшуюся высоту экрана
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { page ->
            OnboardingPageUI(onboardingPages[page])
        }

        // Индикаторы страниц
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            onboardingPages.forEachIndexed { index, _ ->
                Indicator(isSelected = index == pagerState.currentPage)
            }
        }

        // Кнопка для перехода
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 60.dp),
            onClick = {
                if (pagerState.currentPage < onboardingPages.size - 1) {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                } else {
                    viewModel.completeOnboarding()
                    navController.navigate("home")
                }
            },
        ) {
            Text(
                text = if (pagerState.currentPage < onboardingPages.size - 1) "Далее" else "Начать"
            )
        }
    }
}

@Composable
fun OnboardingPageUI(page: OnboardingPage) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = page.imageRes),
            contentDescription = null
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun Indicator(isSelected: Boolean) {
    Box(
        modifier = Modifier
            .size(8.dp)
            .padding(4.dp)
            .background(
                color = if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.secondary,
                shape = MaterialTheme.shapes.small
            )
    )
}
