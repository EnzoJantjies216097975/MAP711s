package com.map711s.namibiahockey.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.SportsHockey
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun EmptyPlayersState(
    onAddPlayer: () -> Unit = {},
    canAddPlayer: Boolean = false
) {
    EmptyStateContent(
        icon = Icons.Default.Person,
        title = "No Players Found",
        description = "There are no players to display. Start by adding some players to your team.",
        primaryAction = if (canAddPlayer) {
            {
                Button(onClick = onAddPlayer) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Player")
                }
            }
        } else null
    )
}

@Composable
fun EmptyTeamsState(
    onCreateTeam: () -> Unit = {},
    canCreateTeam: Boolean = false
) {
    EmptyStateContent(
        icon = Icons.Default.Groups,
        title = "No Teams Found",
        description = "There are no teams to display. Create your first team to get started.",
        primaryAction = if (canCreateTeam) {
            {
                Button(onClick = onCreateTeam) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Create Team")
                }
            }
        } else null
    )
}

@Composable
fun EmptySearchState(
    searchQuery: String,
    onClearSearch: () -> Unit
) {
    EmptyStateContent(
        icon = Icons.Default.SearchOff,
        title = "No Results Found",
        description = "We couldn't find any results for \"$searchQuery\". Try adjusting your search terms.",
        primaryAction = {
            OutlinedButton(onClick = onClearSearch) {
                Text("Clear Search")
            }
        }
    )
}

@Composable
fun NoPermissionState(
    title: String = "Permission Required",
    description: String = "This feature requires additional permissions to work properly.",
    onGrantPermission: () -> Unit
) {
    EmptyStateContent(
        icon = Icons.Default.SportsHockey,
        title = title,
        description = description,
        primaryAction = {
            Button(onClick = onGrantPermission) {
                Text("Grant Permission")
            }
        }
    )
}

@Composable
private fun EmptyStateContent(
    icon: ImageVector,
    title: String,
    description: String,
    primaryAction: (@Composable () -> Unit)? = null,
    secondaryAction: (@Composable () -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )

        if (primaryAction != null || secondaryAction != null) {
            Spacer(modifier = Modifier.height(32.dp))

            primaryAction?.invoke()

            if (secondaryAction != null) {
                Spacer(modifier = Modifier.height(12.dp))
                secondaryAction.invoke()
            }
        }
    }
}