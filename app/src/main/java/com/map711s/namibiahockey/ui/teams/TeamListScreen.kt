package com.map711s.namibiahockey.ui.teams

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SportsHockey
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
fun TeamListScreen(
    onTeamClick: (String) -> Unit = {},
    onAddTeamClick: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var showSearch by remember { mutableStateOf(false) }
    var selectedDivisionFilter by remember { mutableStateOf<String?>(null) }

    val divisions = listOf("All Divisions", "Men's Premier", "Women's Premier", "Men's First", "Women's First", "Junior (U18)", "Junior (U16)")

    Scaffold(
        topBar = {
            if (showSearch) {
                SearchBar(
                    modifier = Modifier.fillMaxWidth(),
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onSearch = { showSearch = false },
                    onClose = {
                        showSearch = false
                        searchQuery = ""
                    },
                    placeholder = { Text("Search teams...") }
                ) {
                    // Search suggestions could go here
                }
            } else {
                TopAppBar(
                    title = { Text("Teams") },
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
                onClick = onAddTeamClick,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Team",
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
            // Division filter chips
            SingleChoiceSegmentedButtonRow(modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth()
            ) {
                divisions.forEachIndexed { index, division ->
                    SegmentedButton(
                        shape = when (index) {
                            0 -> SegmentedButtonDefaults.firstButtonShape
                            divisions.lastIndex -> SegmentedButtonDefaults.lastButtonShape
                            else -> SegmentedButtonDefaults.itemShape
                        },
                        selected = selectedDivisionFilter == division,
                        onClick = {
                            selectedDivisionFilter = if (selectedDivisionFilter == division) null else division
                        }
                    ) {
                        Text(
                            text = if (index == 0) "All" else division.split(" ").last(),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // My Teams Section
            Text(
                text = "My Teams",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
            )

            val myTeams = getSampleTeams().filter { it.isUserTeam }

            if (myTeams.isEmpty()) {
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
                            text = "You don't have any teams yet",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = onAddTeamClick
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Register a Team")
                        }
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(120.dp)
                ) {
                    items(myTeams) { team ->
                        TeamCard(
                            team = team,
                            onClick = { onTeamClick(team.id) }
                        )
                    }
                }
            }

            // All Teams Section
            Text(
                text = "All Teams",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
            )

            // Filter teams based on division selection
            val filteredTeams = if (selectedDivisionFilter == null || selectedDivisionFilter == "All Divisions") {
                getSampleTeams()
            } else {
                getSampleTeams().filter { it.division == selectedDivisionFilter }
            }

            // Filter teams based on search query
            val searchFilteredTeams = if (searchQuery.isBlank()) {
                filteredTeams
            } else {
                filteredTeams.filter { it.name.contains(searchQuery, ignoreCase = true) }
            }

            if (searchFilteredTeams.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No teams found",
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
                    items(searchFilteredTeams) { team ->
                        TeamCard(
                            team = team,
                            onClick = { onTeamClick(team.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TeamCard(
    team: HockeyTeam,
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
            // Team Logo
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (team.logoUrl.isNotBlank()) {
                    AsyncImage(
                        model = team.logoUrl,
                        contentDescription = "${team.name} logo",
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
                                imageVector = Icons.Outlined.SportsHockey,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Team Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = team.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = team.division,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    // Player count
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${team.playerCount} players",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            // Admin badge for user's teams
            if (team.isUserTeam) {
                Badge(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Text(
                        text = "Admin",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }

            if (team.isUserTeam) {
                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = { /* Navigate to team settings */ }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "Team Settings",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

// Data class for Hockey Team
data class HockeyTeam(
    val id: String,
    val name: String,
    val division: String,
    val logoUrl: String = "",
    val playerCount: Int = 0,
    val isUserTeam: Boolean = false
)

// Sample data for teams
fun getSampleTeams(): List<HockeyTeam> {
    return listOf(
        HockeyTeam(
            id = "1",
            name = "Windhoek Warriors",
            division = "Men's Premier",
            logoUrl = "",
            playerCount = 18,
            isUserTeam = true
        ),
        HockeyTeam(
            id = "2",
            name = "Capital Strikers",
            division = "Women's Premier",
            logoUrl = "",
            playerCount = 16,
            isUserTeam = true
        ),
        HockeyTeam(
            id = "3",
            name = "Swakopmund Sharks",
            division = "Men's Premier",
            logoUrl = "",
            playerCount = 17,
            isUserTeam = false
        ),
        HockeyTeam(
            id = "4",
            name = "Coastal Queens",
            division = "Women's Premier",
            logoUrl = "",
            playerCount = 15,
            isUserTeam = false
        ),
        HockeyTeam(
            id = "5",
            name = "Northern Tigers",
            division = "Men's First",
            logoUrl = "",
            playerCount = 14,
            isUserTeam = false
        ),
        HockeyTeam(
            id = "6",
            name = "Eastern Eagles",
            division = "Women's First",
            logoUrl = "",
            playerCount = 16,
            isUserTeam = false
        ),
        HockeyTeam(
            id = "7",
            name = "Windhoek Juniors",
            division = "Junior (U18)",
            logoUrl = "",
            playerCount = 12,
            isUserTeam = false
        ),
        HockeyTeam(
            id = "8",
            name = "Swakopmund Youth",
            division = "Junior (U16)",
            logoUrl = "",
            playerCount = 14,
            isUserTeam = false
        )
    )
}