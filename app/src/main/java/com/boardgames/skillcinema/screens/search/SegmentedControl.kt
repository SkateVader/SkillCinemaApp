package com.boardgames.skillcinema.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun SegmentedControl(
    items: List<String>,
    selectedItem: String,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .border(
                width = 1.dp,
                color = Color.Black,
                shape = MaterialTheme.shapes.medium
            )
    ) {
        items.forEachIndexed { index, item ->
            val isSelected = (item == selectedItem)

            Box(
                modifier = Modifier
                    .weight(if (item == "Популярность") 1.2f else 1f) // Увеличиваем ширину для длинного текста
                    .clickable { onItemSelected(item) }
                    .background(
                        if (isSelected) Color(0xFF3F51B5) // Синий цвет заливки
                        else Color.Transparent
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp), // Уменьшаем отступы
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item,
                    color = if (isSelected) Color.White else Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }

            if (index < items.size - 1) {
                Divider(
                    color = Color.Black,
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                )
            }
        }
    }
}
