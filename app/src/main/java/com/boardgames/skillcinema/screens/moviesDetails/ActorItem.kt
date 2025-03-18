package com.boardgames.skillcinema.screens.moviesDetails

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.boardgames.skillcinema.data.remote.CastResponse

@Composable
fun ActorItem(actor: CastResponse, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = actor.posterUrl,
            contentDescription = actor.nameRu,
            modifier = Modifier.size(120.dp)
        )
        Text(
            text = actor.nameRu.toString(),
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1
        )
        Text(
            text = actor.role ?: "",
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1
        )
    }
}