package com.boardgames.skillcinema.screens.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import java.time.LocalDate
import com.boardgames.skillcinema.navigation.BottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeriodSelectionScreen(navController: NavController) {
    val parentEntry = remember(navController.currentBackStackEntry) {
        navController.getBackStackEntry("search_settings")
    }
    val viewModel: SearchSettingsViewModel = hiltViewModel(parentEntry)
    val currentYear = LocalDate.now().year
    val minYear = 1895
    val pageSize = 12
    var startPage by remember { mutableStateOf(0) }
    var selectedStartYear by remember { mutableStateOf<Int?>(null) }
    var endPage by remember { mutableStateOf(0) }
    var selectedEndYear by remember { mutableStateOf<Int?>(null) }
    val allYears = (minYear..currentYear).toList()
    val startYears = allYears.chunked(pageSize).getOrNull(startPage) ?: emptyList()
    val endYears = allYears.chunked(pageSize).getOrNull(endPage) ?: emptyList()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Период",
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                YearSelectionBlock(
                    title = "Искать в период с",
                    years = startYears,
                    selectedYear = selectedStartYear,
                    onYearSelected = { selectedStartYear = it },
                    onPageChange = { startPage = it },
                    pageCount = (allYears.size + pageSize - 1) / pageSize
                )
            }
            item {
                YearSelectionBlock(
                    title = "Искать в период до",
                    years = endYears,
                    selectedYear = selectedEndYear,
                    onYearSelected = { selectedEndYear = it },
                    onPageChange = { endPage = it },
                    pageCount = (allYears.size + pageSize - 1) / pageSize
                )
            }
            item {
                // Новая строка с двумя кнопками
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            // Устанавливаем значение периода по умолчанию (например, сбрасываем фильтр)
                            viewModel.updatePeriod("")
                            navController.navigateUp()
                        }
                    ) {
                        Text("Любой период")
                    }

                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            if (selectedStartYear != null && selectedEndYear != null) {
                                // Устанавливаем выбранный период в формате "годС - годДо"
                                viewModel.updatePeriod("${selectedStartYear} -" +
                                        " ${selectedEndYear}")
                                navController.navigateUp()
                            }
                        },
                        enabled = selectedStartYear != null && selectedEndYear != null
                    ) {
                        Text("Применить фильтр")
                    }
                }
            }
        }
    }
}

@Composable
private fun YearSelectionBlock(
    title: String,
    years: List<Int>,
    selectedYear: Int?,
    onYearSelected: (Int) -> Unit,
    onPageChange: (Int) -> Unit,
    pageCount: Int
) {
    var currentPage by remember { mutableStateOf(0) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Страница ${currentPage + 1}/$pageCount",
                    style = MaterialTheme.typography.bodySmall
                )
                IconButton(onClick = {
                    if (currentPage > 0) {
                        currentPage--
                        onPageChange(currentPage)
                    }
                }) {
                    Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Назад")
                }
                IconButton(onClick = {
                    if (currentPage < pageCount - 1) {
                        currentPage++
                        onPageChange(currentPage)
                    }
                }) {
                    Icon(Icons.Default.ArrowForwardIos, contentDescription = "Вперед")
                }
            }
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 400.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(years) { year ->
                YearItem(
                    year = year,
                    isSelected = year == selectedYear,
                    onSelect = { onYearSelected(year) }
                )
            }
        }
    }
}

@Composable
private fun YearItem(
    year: Int,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$year",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
