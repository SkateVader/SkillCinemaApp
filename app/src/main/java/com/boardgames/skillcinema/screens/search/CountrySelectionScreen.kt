package com.boardgames.skillcinema.screens.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.boardgames.skillcinema.navigation.BottomNavigationBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountrySelectionScreen(navController: NavController) {
    val parentEntry = remember(navController.currentBackStackEntry) {
        navController.getBackStackEntry("search_settings")
    }
    val viewModel: SearchSettingsViewModel = hiltViewModel(parentEntry)

    // Получаем список стран из ViewModel (список объектов Country)
    val countries by viewModel.countries.collectAsState()
    var searchText by remember { mutableStateOf("") }

    // Добавляем "Любая страна" в начало списка
    val filteredCountries =
        listOf(com.boardgames.skillcinema.data.remote.Country(name = "Любая страна", id = null)) +
            countries.filter {
                it.name?.contains(searchText, ignoreCase = true) == true
            }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // Центрированный заголовок с отступами, как на экране "Настройки поиска"
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Страна",
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Center
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    Spacer(modifier = Modifier.width(48.dp))
                }
            )
        },
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp)
        ) {
            item {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    placeholder = { Text("Поиск страны") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            items(filteredCountries) { country ->
                Text(
                    text = country.name ?: "",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            // Если выбран "Любая страна", сохраняем null, иначе – выбранное значение (ID)
                            val selectedId = if (country.name == "Любая страна")
                                null else country.id
                            viewModel.updateCountry(selectedId)
                            navController.navigateUp()
                        }
                        .padding(16.dp)
                )
                Divider()
            }
        }
    }
}
