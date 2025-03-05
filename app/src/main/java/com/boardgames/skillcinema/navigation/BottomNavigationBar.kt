package com.boardgames.skillcinema.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavigationBar(navController: NavController) {
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination

    NavigationBar {
        // Главная
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Главная") },
            selected = currentDestination?.route == "home",
            onClick = { navController.navigate("home") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurface,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedTextColor = MaterialTheme.colorScheme.onSurface,
                indicatorColor = Color.Transparent
            )
        )

        // Поиск
        NavigationBarItem(
            icon = { Icon(Icons.Default.Search, contentDescription = "Поиск") },
            selected = currentDestination?.route == "search",
            onClick = { navController.navigate("search") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurface,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedTextColor = MaterialTheme.colorScheme.onSurface,
                indicatorColor = Color.Transparent
            )
        )

        // Профиль
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Профиль") },
            selected = currentDestination?.route == "profile",
            onClick = { navController.navigate("profile") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurface,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedTextColor = MaterialTheme.colorScheme.onSurface,
                indicatorColor = Color.Transparent
            )
        )
    }
}
