package com.boardgames.skillcinema.screens.moviesDetails

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.boardgames.skillcinema.data.remote.CrewResponse

@Composable
fun CrewGridSection(
    crew: List<CrewResponse>,
    sectionTitle: String = "Над фильмом работали",
    onCrewClick: (CrewResponse) -> Unit,
    onMoreClick: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(sectionTitle, style = MaterialTheme.typography.titleMedium)
            if (crew.size > 20) {
                Row(
                    modifier = Modifier.clickable { onMoreClick() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = crew.size.toString(), style = MaterialTheme.typography.bodyMedium)
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Смотреть всех"
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        val displayCrew = if (crew.size > 20) crew.take(20) else crew
        LazyHorizontalGrid(
            rows = GridCells.Fixed(4),
            modifier = Modifier.height((4 * 120).dp),
            contentPadding = PaddingValues(4.dp)
        ) {
            items(displayCrew.size) { index ->
                val crewMember = displayCrew[index]
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .clickable { onCrewClick(crewMember) }
                        .width(200.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = crewMember.posterUrl,
                            contentDescription = crewMember.nameRu,
                            modifier = Modifier.size(80.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = crewMember.nameRu.toString(),
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = crewMember.profession ?: "",
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}
