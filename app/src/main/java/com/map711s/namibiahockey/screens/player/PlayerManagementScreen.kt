package com.map711s.namibiahockey.screens.player

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.map711s.namibiahockey.data.model.PlayerListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerManagementScreen(
    onNavigateBack: () -> Unit
){
    var searchQuery by remember { mutableStateOf("") }
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("My Team", "All Players")

    // Sample players for demonstration
    val players = remember {
        listOf(
            PlayerListItem(
                id = "1",
                name = "John Doe",
                position = "Forward",
                teamName = "Windhoek Warriors",
                jerseyNumber = 10
            ),
            PlayerListItem(
                id = "2",
                name = "Jane Smith",
                position = "Goalkeeper",
                teamName = "Windhoek Warriors",
                jerseyNumber = 1
            ),
            PlayerListItem(
                id = "3",
                name = "Michael Brown",
                position = "Defender",
                teamName = "Windhoek Warriors",
                jerseyNumber = 4
            ),
            PlayerListItem(
                id = "4",
                name = "Sarah Johnson",
                position = "Midfielder",
                teamName = "Windhoek Warriors",
                jerseyNumber = 7
            )
        )
    }

    // Filtered players based on search
    val filteredPlayers = if (searchQuery.isBlank()) {
        players
    } else {
        players.filter { it.name.contains(searchQuery, ignoreCase = true) ||
                    it.position.contains(searchQuery, ignoreCase = true) ||
                    it.teamName.contains(searchQuery, ignoreCase = true) ||
                    it.jerseyNumber.toString().contains(searchQuery)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Player Management") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick =  { /* Open add player dialog */ },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Player",
                    tint = Color.White
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tabs for filtering by team or all players
            TabRow(
                selectedTabIndex = selectedTabIndex
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            // Search field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search players...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon"
                    )
                },
                singleLine = true
            )

            // Player list
            if (filteredPlayers.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No players found",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredPlayers) { player ->
                        PlayerListItemCard(
                            player = player,
                            onEditClick = { /* Open edit player dialog */ },
                            onDeleteClick = { /* Show delete confirmation dialog */ }
                        )
                    }

                    // Add some space at the bottom for the FAB
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun PlayerListItemCard(
    player: PlayerListItem,
    onEditClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Navigate to player details */ },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Player avatar/icon
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(30.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Player info
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = player.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "${player.position} | #${player.jerseyNumber}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Text(
                text = player.teamName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }

        // Action buttons
        IconButton(
            onClick = { onEditClick(player.id) }
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit Player",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        IconButton(
            onClick = { onDeleteClick(player.id) }
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete Player",
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
    }
}

// Dialog for adding/editing a player would be defined here
// It would include fields for name, position, jersey number, etc.