package com.boardgames.skillcinema.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.boardgames.skillcinema.screens.onboarding.OnboardingScreen
import com.boardgames.skillcinema.screens.home.HomeScreen
import com.boardgames.skillcinema.screens.details.MovieDetailsScreen
import com.boardgames.skillcinema.screens.collections.CollectionsScreen
import com.boardgames.skillcinema.screens.fullcollection.FullCollectionScreen
import com.boardgames.skillcinema.screens.search.CountrySelectionScreen
import com.boardgames.skillcinema.screens.search.GenreSelectionScreen
import com.boardgames.skillcinema.screens.search.PeriodSelectionScreen
import com.boardgames.skillcinema.screens.search.SearchScreen
import com.boardgames.skillcinema.screens.search.SearchSettingsScreen

@Composable
fun NavGraph(startDestination: String) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = startDestination) {
        composable("onboarding") { OnboardingScreen(navController) }
        composable("home") { HomeScreen(navController) }
        composable("details/{id}") { backStackEntry ->
            val movieId = backStackEntry.arguments?.getString("id")?.toIntOrNull()
            if (movieId != null) {
                MovieDetailsScreen(navController, movieId)
            }
        }
        composable("profile") { CollectionsScreen(navController) }
        composable("search") { SearchScreen(navController) }
        composable("search_settings") { SearchSettingsScreen(navController) }
        composable("search_country") { CountrySelectionScreen(navController) }
        composable("search_genre") { GenreSelectionScreen(navController) }
        composable("search_period") { PeriodSelectionScreen(navController) }
        composable("fullCollection/{collectionTitle}/{collectionType}") { backStackEntry ->
            val collectionTitle = backStackEntry.arguments?.getString("collectionTitle") ?: ""
            FullCollectionScreen(
                navController,
                collectionTitle = collectionTitle
            )
        }
    }
}
