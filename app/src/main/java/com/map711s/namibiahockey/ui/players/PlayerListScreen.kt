package com.map711s.namibiahockey.ui.players

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerListScreen(
    onPlayerClick: (String) -> Unit = {},
    onAddPlayerClick: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var showSearch by remember { mutableStateOf(false) }
    var selectedTeamFilter by remember { mutableStateOf<String?>(null) }
    var selectedPositionFilter by remember { mutableStateOf<String?>(null) }

    // Get user's teams for filtering
    val userTeams = getSampleTeams().filter { it.isUserTeam }

    // List of positions for filtering
    val positions = listOf("All Positions", "Forward", "Midfielder", "Defender", "Goalkeeper")

    Scaffold(
        topBar = {
            if (showSearch) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onSearch = { showSearch = false },
                    onClose = {
                        showSearch = false
                        searchQuery = ""
                    },
                    placeholder = { Text("Search players...") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Search suggestions could go here
                }
            } else {
                TopAppBar(
                    title = { Text("Players") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    actions = {
                        IconButton(onClick = { showSearch = true }) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddPlayerClick,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Player",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Filters section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Team filter
                ExposedDropdownMenuBox(
                    expanded = false,
                    onExpandedChange = { },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = selectedTeamFilter ?: "All Teams",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Team") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = false)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        singleLine = true
                    )

                    DropdownMenu(
                        expanded = false,
                        onDismissRequest = { }
                    ) {
                        DropdownMenuItem(
                            text = { Text("All Teams") },
                            onClick = { selectedTeamFilter = null }
                        )

                        userTeams.forEach { team ->
                            DropdownMenuItem(
                                text = { Text(team.name) },
                                onClick = { selectedTeamFilter = team.id }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Position filter
                ExposedDropdownMenuBox(
                    expanded = false,
                    onExpandedChange = { },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = selectedPositionFilter ?: "All Positions",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Position") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = false)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        singleLine = true
                    )

                    DropdownMenu(
                        expanded = false,
                        onDismissRequest = { }
                    ) {
                        positions.forEach { position ->
                            DropdownMenuItem(
                                text = { Text(position) },
                                onClick = {
                                    selectedPositionFilter = if (position == "All Positions") null else position
                                }
                            )
                        }
                    }
                }
            }

            // My Players Section
            Text(
                text = "My Team Players",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
            )

            val myPlayers = getSamplePlayers().filter { it.isOnUserTeam }

            if (myPlayers.isEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "You don't have any players on your teams yet",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = onAddPlayerClick
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Player")
                        }
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(170.dp)
                ) {
                    items(myPlayers) { player ->
                        PlayerCard(
                            player = player,
                            onClick = { onPlayerClick(player.id) }
                        )
                    }
                }
            }

            // All Players Section
            Text(
                text = "All Players",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
            )

            // Filter players based on team and position selection
            val filteredPlayers = getSamplePlayers().filter { player ->
                (selectedTeamFilter == null || player.teamId == selectedTeamFilter) &&
                        (selectedPositionFilter == null || player.position == selectedPositionFilter)
            }

            // Filter players based on search query
            val searchFilteredPlayers = if (searchQuery.isBlank()) {
                filteredPlayers
            } else {
                filteredPlayers.filter {
                    it.name.contains(searchQuery, ignoreCase = true) ||
                            it.teamName.contains(searchQuery, ignoreCase = true)
                }
            }

            if (searchFilteredPlayers.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No players found",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = 32.dp)
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(searchFilteredPlayers) { player ->
                        PlayerCard(
                            player = player,
                            onClick = { onPlayerClick(player.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PlayerCard(
    player: HockeyPlayer,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Player Photo
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (player.photoUrl.isNotBlank()) {
                    AsyncImage(
                        model = player.photoUrl,
                        contentDescription = "${player.name} photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Outlined.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Player Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = player.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Team
                    Icon(
                        imageVector = Icons.Outlined.Group,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = player.teamName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Position
                    Surface(
                        color = getPositionColor(player.position).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = player.position,
                            style = MaterialTheme.typography.labelSmall,
                            color = getPositionColor(player.position),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Player stats
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Player number
                    Icon(
                        imageVector = Icons.Outlined.Tag,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "#${player.jerseyNumber}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    if (player.goals > 0 || player.assists > 0) {
                        Spacer(modifier = Modifier.width(16.dp))

                        // Goals
                        Icon(
                            imageVector = Icons.Outlined.SportsSoccer,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${player.goals}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // Assists
                        Text(
                            text = "A: ${player.assists}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            // Captain badge
            if (player.isCaptain) {
                Badge(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Text(
                        text = "C",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }
        }
    }
}

// Position color coding
fun getPositionColor(position: String): Color {
    return when (position) {
        "Forward" -> Color(0xFFD32F2F) // Red
        "Midfielder" -> Color(0xFF1976D2) // Blue
        "Defender" -> Color(0xFF388E3C) // Green
        "Goalkeeper" -> Color(0xFF7B1FA2) // Purple
        else -> Color(0xFF757575) // Gray
    }
}

// Data class for Hockey Player
data class HockeyPlayer(
    val id: String,
    val name: String,
    val teamId: String,
    val teamName: String,
    val position: String,
    val jerseyNumber: Int,
    val photoUrl: String = "",
    val goals: Int = 0,
    val assists: Int = 0,
    val isCaptain: Boolean = false,
    val isOnUserTeam: Boolean = false
)

// Data class for Team
data class Team(
    val id: String,
    val name: String,
    val isUserTeam: Boolean
)

// Sample data for teams with IDs matching player teams
fun getSampleTeams(): List<Team> {
    return listOf(
        Team("team1", "Windhoek Warriors", true),
        Team("team2", "Capital Strikers", true),
        Team("team3", "Swakopmund Sharks", false),
        Team("team4", "Coastal Queens", false),
        Team("team5", "Northern Tigers", false)
    )
}

// Sample data for players
fun getSamplePlayers(): List<HockeyPlayer> {
    return listOf(
        HockeyPlayer(
            id = "1",
            name = "Johannes Smith",
            teamId = "team1",
            teamName = "Windhoek Warriors",
            position = "Forward",
            jerseyNumber = 10,
            goals = 12,
            assists = 5,
            isCaptain = true,
            isOnUserTeam = true
        ),
        HockeyPlayer(
            id = "2",
            name = "David Nangolo",
            teamId = "team1",
            teamName = "Windhoek Warriors",
            position = "Midfielder",
            jerseyNumber = 8,
            goals = 4,
            assists = 9,
            isOnUserTeam = true
        ),
        HockeyPlayer(
            id = "3",
            name = "Maria Shilongo",
            teamId = "team2",
            teamName = "Capital Strikers",
            position = "Defender",
            jerseyNumber = 4,
            goals = 1,
            assists = 2,
            isOnUserTeam = true
        ),
        HockeyPlayer(
            id = "4",
            name = "Elizabeth Uushona",
            teamId = "team2",
            teamName = "Capital Strikers",
            position = "Goalkeeper",
            jerseyNumber = 1,
            isCaptain = true,
            isOnUserTeam = true
        ),
        HockeyPlayer(
            id = "5",
            name = "Michael Tjituka",
            teamId = "team3",
            teamName = "Swakopmund Sharks",
            position = "Forward",
            jerseyNumber = 11,
            goals = 15,
            assists = 3
        ),
        HockeyPlayer(
            id = "6",
            name = "Sara Kapere",
            teamId = "team4",
            teamName = "Coastal Queens",
            position = "Midfielder",
            jerseyNumber = 7,
            goals = 6,
            assists = 10
        ),
        HockeyPlayer(
            id = "7",
            name = "James Goagoseb",
            teamId = "team3",
            teamName = "Swakopmund Sharks",
            position = "Defender",
            jerseyNumber = 3,
            goals = 0,
            assists = 1
        ),
        HockeyPlayer(
            id = "8",
            name = "Thomas Shivute",
            teamId = "team5",
            teamName = "Northern Tigers",
            position = "Forward",
            jerseyNumber = 9,
            goals = 8,
            assists = 7
        ),
        HockeyPlayer(
            id = "9",
            name = "Lucia Nekwaya",
            teamId = "team4",
            teamName = "Coastal Queens",
            position = "Defender",
            jerseyNumber = 5,
            goals = 2,
            assists = 1,
            isCaptain = true
        ),
        HockeyPlayer(
            id = "10",
            name = "Peter Nasima",
            teamId = "team5",
            teamName = "Northern Tigers",
            position = "Goalkeeper",
            jerseyNumber = 1,
            goals = 0,
            assists = 0
        )
    )
}