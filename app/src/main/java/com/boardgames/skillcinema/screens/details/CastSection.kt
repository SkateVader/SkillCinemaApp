package com.boardgames.skillcinema.screens.details

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.boardgames.skillcinema.data.remote.CastMember

@Composable
fun CastSection(cast: List<CastMember>) {
    Column {
        Text("Актерский состав", style = MaterialTheme.typography.titleMedium)
        Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
            cast.forEach { actor ->
                Column(modifier = Modifier.padding(8.dp)) {
                    Image(
                        painter = rememberImagePainter(actor.photoUrl),
                        contentDescription = actor.name,
                        modifier = Modifier.size(80.dp)
                    )
                    Text(text = actor.name, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
