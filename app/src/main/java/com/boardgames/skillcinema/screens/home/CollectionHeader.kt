package com.boardgames.skillcinema.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CollectionHeader(title: String, onViewAll: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth()) {
        // Название подборки слева с отступом 10dp
        Text(
            text = title,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 15.dp, bottom = 15.dp)
        )
        // Кнопка "Все" справа с отступом 10dp
        Text(
            text = "Все",
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 15.dp, bottom = 15.dp)
                .clickable { onViewAll() }
        )
    }
}
