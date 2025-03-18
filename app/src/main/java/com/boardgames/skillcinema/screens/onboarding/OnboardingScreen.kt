package com.boardgames.skillcinema.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.boardgames.skillcinema.screens.onboarding.OnboardingPage
import com.boardgames.skillcinema.screens.onboarding.OnboardingViewModel
import com.boardgames.skillcinema.screens.onboarding.onboardingPages
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(navController: NavController, initialPage: Int = 0) {
    // Всего страниц: 3 обычные + 1 загрузочная = 4
    val totalPages = onboardingPages.size + 1
    val pagerState = rememberPagerState(initialPage =
    initialPage, pageCount = { totalPages })
    val coroutineScope = rememberCoroutineScope()

    // Если мы находимся не на загрузочной странице – отображаем внешний Scaffold
    if (pagerState.currentPage < totalPages - 1) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Skillcinema") },
                    actions = {
                        if (pagerState.currentPage < totalPages - 1) {
                            TextButton(
                                onClick = {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(totalPages - 1)
                                    }
                                }
                            ) {
                                Text(text = "Пропустить", color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            },
            content = { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        if (page < onboardingPages.size) {
                            OnboardingPageUI(page = onboardingPages[page])
                        } else {
                            // На загрузочной странице внешний Scaffold не используется
                            OnboardingLoadingPageUI(
                                navController = navController,
                                isCurrentPage = (page == pagerState.currentPage)
                            )
                        }
                    }
                    if (pagerState.currentPage < totalPages - 1) {
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(start = 30.dp, bottom = 40.dp),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            repeat(onboardingPages.size) { index ->
                                Indicator(isSelected = index == pagerState.currentPage)
                            }
                        }
                    }
                }
            }
        )
    } else {
        // Если текущая страница – загрузочная (4-я), отображаем только её контент без внешнего Scaffold
        OnboardingLoadingPageUI(
            navController = navController,
            isCurrentPage = true
        )
    }
}

@Composable
fun OnboardingPageUI(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = page.imageRes),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        val descriptionText = if (!page.description.contains("\n"))
            page.description + "\n" else page.description
        Text(
            text = descriptionText,
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 32.sp),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 100.dp)
                .padding(start = 30.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingLoadingPageUI(
    navController: NavController,
    isCurrentPage: Boolean = true,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val isDataLoaded by homeViewModel.isDataLoaded.collectAsState()
    val viewModel: OnboardingViewModel = hiltViewModel()

    // Переход на home выполняется только, если эта страница является загрузочной (4-я) и данные загружены
    LaunchedEffect(isDataLoaded, isCurrentPage) {
        if (isCurrentPage && isDataLoaded) {
            delay(2000)
            viewModel.completeOnboarding()
            navController.navigate("home") {
                popUpTo("onboarding_loading") { inclusive = true }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Skillcinema") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
                Image(
                    painter = painterResource(id = onboardingPages[0].imageRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )
            }
        }
    )
}

@Composable
fun Indicator(
    isSelected: Boolean,
    size: Dp = 10.dp,
    horizontalPadding: Dp = 4.dp
) {
    Box(
        modifier = Modifier
            .padding(horizontal = horizontalPadding)
            .size(8.dp)
            .wrapContentSize(Alignment.Center)
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                )
                .background(
                    color =
                    if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                    shape = CircleShape
                )
        )
    }
}
