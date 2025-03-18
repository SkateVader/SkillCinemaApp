package com.boardgames.skillcinema.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.boardgames.skillcinema.screens.moviesDetails.MovieDetailsScreen
import com.boardgames.skillcinema.screens.collections.CollectionsScreen
import com.boardgames.skillcinema.screens.filmography.FilmographyScreen
import com.boardgames.skillcinema.screens.fullActors.FullActorsScreen
import com.boardgames.skillcinema.screens.fullBestCollections.FullBestMoviesScreen
import com.boardgames.skillcinema.screens.fullCollection.FullCollectionScreen
import com.boardgames.skillcinema.screens.fullCrew.FullCrewScreen
import com.boardgames.skillcinema.screens.fullImage.FullImageScreen
import com.boardgames.skillcinema.screens.gallery.MovieImagesScreen
import com.boardgames.skillcinema.screens.home.HomeScreen
import com.boardgames.skillcinema.screens.home.OnboardingLoadingPageUI
import com.boardgames.skillcinema.screens.home.OnboardingScreen
import com.boardgames.skillcinema.screens.personDetails.PersonDetailScreen
import com.boardgames.skillcinema.screens.search.CountrySelectionScreen
import com.boardgames.skillcinema.screens.search.GenreSelectionScreen
import com.boardgames.skillcinema.screens.search.PeriodSelectionScreen
import com.boardgames.skillcinema.screens.search.SearchScreen
import com.boardgames.skillcinema.screens.search.SearchSettingsScreen
import com.boardgames.skillcinema.screens.series.SeriesEpisodesScreen

@Composable
fun NavGraph(startDestination: String) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = startDestination) {
        composable("onboarding") { OnboardingScreen(navController) }
        composable("onboarding_loading") { OnboardingLoadingPageUI(navController) }
        composable("home") { HomeScreen(navController) }
        composable("details/{id}") { backStackEntry ->
            val movieId = backStackEntry.arguments?.getString("id")?.toIntOrNull()
            if (movieId != null) {
                MovieDetailsScreen(navController, movieId)
            }
        }
        composable("seriesEpisodes/{movieId}/{seriesTitle}/{seasonsCount}") {
            backStackEntry ->
            val movieId =
                backStackEntry.arguments?.getString("movieId")?.toIntOrNull() ?: 0
            val seriesTitle = backStackEntry.arguments?.getString("seriesTitle") ?: ""
            val seasonsCount =
                backStackEntry.arguments?.getString("seasonsCount")?.toIntOrNull() ?: 0
            SeriesEpisodesScreen(navController, movieId, seriesTitle, seasonsCount)
        }
        composable("profile") { CollectionsScreen(navController) }
        composable("search") { SearchScreen(navController) }
        composable("search_settings") { SearchSettingsScreen(navController) }
        composable("search_country") { CountrySelectionScreen(navController) }
        composable("search_genre") { GenreSelectionScreen(navController) }
        composable("search_period") { PeriodSelectionScreen(navController) }
        composable(
            "fullCollection/{collectionTitle}/{collectionType}/{movieId}",
            arguments = listOf(
                navArgument("collectionTitle") { type = NavType.StringType },
                navArgument("collectionType") { type = NavType.StringType },
                navArgument("movieId") { type = NavType.IntType; defaultValue = 0 }
            )
        ) { backStackEntry ->
            val collectionTitle =
                backStackEntry.arguments?.getString("collectionTitle") ?: ""
            val collectionType =
                backStackEntry.arguments?.getString("collectionType") ?: ""
            val movieId = backStackEntry.arguments?.getInt("movieId") ?: 0
            FullCollectionScreen(
                navController,
                collectionTitle = collectionTitle,
                collectionType = collectionType,
                movieId = movieId
            )
        }
        composable("fullActors/{movieId}") { backStackEntry ->
            val movieId =
                backStackEntry.arguments?.getString("movieId")?.toIntOrNull() ?: 0
            FullActorsScreen(navController, movieId)
        }
        composable("personDetails/{staffId}") { backStackEntry ->
            val staffId = backStackEntry.arguments?.getString("staffId")?.toIntOrNull()
            PersonDetailScreen(navController, staffId)
        }
        composable("fullBestMovies/{staffId}") { backStackEntry ->
            val staffId =
                backStackEntry.arguments?.getString("staffId")?.toIntOrNull() ?: 0
            FullBestMoviesScreen(navController, staffId)
        }
        composable("fullCrew/{movieId}") { backStackEntry ->
            val movieId =
                backStackEntry.arguments?.getString("movieId")?.toIntOrNull() ?: 0
            FullCrewScreen(navController, movieId)
        }
        composable("filmography/{actorId}") { backStackEntry ->
            val actorId =
                backStackEntry.arguments?.getString("actorId")?.toIntOrNull() ?: 0
            FilmographyScreen(navController, actorId)
        }
        composable(
            route =
            "fullScreenImage?movieId={movieId}&type={type}&initialImageUrl={initialImageUrl}",
            arguments = listOf(
                navArgument("movieId") { type = NavType.IntType },
                navArgument("type") { type = NavType.StringType },
                navArgument("initialImageUrl") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getInt("movieId") ?: 0
            val type = backStackEntry.arguments?.getString("type") ?: "STILL"
            val initialImageUrl =
                backStackEntry.arguments?.getString("initialImageUrl") ?: ""
            FullImageScreen(navController, movieId, type, initialImageUrl)
        }
        composable(
            route = "movieImagesScreen/{movieId}",
            arguments = listOf(
                navArgument("movieId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getInt("movieId") ?: 0
            MovieImagesScreen(navController, movieId)
        }
    }
}
