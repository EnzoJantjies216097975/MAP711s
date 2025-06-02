package com.map711s.namibiahockey.screens.team

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.map711s.namibiahockey.data.model.HockeyType
import com.map711s.namibiahockey.data.model.Team
import com.map711s.namibiahockey.data.model.UserRole
import com.map711s.namibiahockey.viewmodel.AuthViewModel
import com.map711s.namibiahockey.viewmodel.TeamViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamsScreen(
    hockeyType: HockeyType,
    onNavigateBack: () -> Unit,
    onNavigateToCreateTeam: () -> Unit,
    onNavigateToTeamDetails: (String) -> Unit,
    onNavigateToPlayerManagement: (String) -> Unit = {},
    teamViewModel: TeamViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("All Teams", "My Teams", "National Teams", "Club Teams")

    val teamListState by teamViewModel.teamListState.collectAsState()
    val enhancedTeams = teamListState.teams
    val userProfileState by authViewModel.userProfileState.collectAsState()
    val currentUser = userProfileState.user
    val isAdmin = currentUser?.role == UserRole.ADMIN
    val isCoachOrManager = currentUser?.role == UserRole.COACH || currentUser?.role == UserRole.MANAGER

    // Dialog states
    var showDeleteDialog by remember { mutableStateOf(false) }
    var teamToDelete by remember { mutableStateOf<Team?>(null) }
    var selectedTeam by remember { mutableStateOf<Team?>(null) }
    var showTeamActionsMenu by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Load teams when screen is displayed
    LaunchedEffect(Unit) {
        teamViewModel.loadAllTeams()
    }

    // Enhanced sample teams with more details
    val enhancedTeam = remember {
        listOf(
            Team(
                id = "1",
                name = "Windhoek Warriors",
                hockeyType = HockeyType.OUTDOOR,
                category = "Men's",
                division = "Premier League",
                coach = "John Smith",
                manager = "Sarah Johnson",
                playerCount = 22,
                isNationalTeam = false,
                founded = 2010,
                homeVenue = "Independence Stadium",
                wins = 15,
                losses = 3,
                draws = 2,
                points = 47,
                ranking = 1,
                logoUrl = "",
                isActive = true,
                description = "Premier men's outdoor hockey team based in Windhoek"
            ),
            Team(
                id = "2",
                name = "Namibia Men's National Team",
                hockeyType = HockeyType.OUTDOOR,
                category = "Men's",
                division = "National",
                coach = "Michael Brown",
                manager = "David Wilson",
                playerCount = 18,
                isNationalTeam = true,
                founded = 1990,
                homeVenue = "National Hockey Stadium",
                wins = 25,
                losses = 8,
                draws = 5,
                points = 80,
                ranking = 1,
                logoUrl = "",
                isActive = true,
                description = "Official Namibia Men's National Hockey Team"
            ),
            Team(
                id = "3",
                name = "Swakopmund Seagulls",
                hockeyType = HockeyType.OUTDOOR,
                category = "Women's",
                division = "First Division",
                coach = "Lisa Anderson",
                manager = "Emma Roberts",
                playerCount = 20,
                isNationalTeam = false,
                founded = 2015,
                homeVenue = "Swakopmund Sports Complex",
                wins = 12,
                losses = 6,
                draws = 2,
                points = 38,
                ranking = 3,
                logoUrl = "",
                isActive = true,
                description = "Coastal women's hockey team from Swakopmund"
            ),
            Team(
                id = "4",
                name = "Indoor Lions",
                hockeyType = HockeyType.INDOOR,
                category = "Mixed",
                division = "Indoor Premier",
                coach = "Alex Turner",
                manager = "Jordan Lee",
                playerCount = 16,
                isNationalTeam = false,
                founded = 2018,
                homeVenue = "Indoor Sports Arena",
                wins = 18,
                losses = 4,
                draws = 1,
                points = 55,
                ranking = 1,
                logoUrl = "",
                isActive = true,
                description = "Top indoor hockey team specializing in fast-paced gameplay"
            ),
            Team(
                id = "5",
                name = "Youth Development FC",
                hockeyType = HockeyType.OUTDOOR,
                category = "Youth",
                division = "Development League",
                coach = "Mark Thompson",
                manager = "Rachel Green",
                playerCount = 25,
                isNationalTeam = false,
                founded = 2020,
                homeVenue = "Youth Development Center",
                wins = 8,
                losses = 10,
                draws = 4,
                points = 28,
                ranking = 5,
                logoUrl = "",
                isActive = true,
                description = "Developing young talent for future hockey success"
            )
        )
    }


    // Filter teams based on search and tab
    val filteredTeams = if (searchQuery.isBlank()) {
        when (selectedTabIndex) {
            0 -> enhancedTeams.filter { it.hockeyType == hockeyType || hockeyType == HockeyType.BOTH }
            1 -> enhancedTeams.filter {
                // Mock: Teams where current user is coach/manager
                it.coach.contains(currentUser?.name?.split(" ")?.firstOrNull() ?: "", ignoreCase = true) ||
                        it.manager.contains(currentUser?.name?.split(" ")?.firstOrNull() ?: "", ignoreCase = true)
            }
            2 -> enhancedTeams.filter { it.isNationalTeam }
            3 -> enhancedTeams.filter { !it.isNationalTeam }
            else -> enhancedTeams
        }
    } else {
        enhancedTeams.filter { team ->
            (team.name.contains(searchQuery, ignoreCase = true) ||
                    team.category.contains(searchQuery, ignoreCase = true) ||
                    team.division.contains(searchQuery, ignoreCase = true) ||
                    team.coach.contains(searchQuery, ignoreCase = true) ||
                    team.manager.contains(searchQuery, ignoreCase = true)) &&
                    (team.hockeyType == hockeyType || hockeyType == HockeyType.BOTH)
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Team Management")
                        Text(
                            text = "${hockeyType.name.lowercase().replaceFirstChar { it.uppercase() }} Hockey",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Filter functionality */ }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            if (isAdmin || isCoachOrManager) {
                FloatingActionButton(
                    onClick = onNavigateToCreateTeam,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Create Team",
                        tint = Color.White
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Enhanced Statistics Header
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "League Overview",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TeamStatItem(
                            label = "Total Teams",
                            value = filteredTeams.size.toString(),
                            icon = Icons.Default.Groups
                        )
                        TeamStatItem(
                            label = "National Teams",
                            value = filteredTeams.count { it.isNationalTeam }.toString(),
                            icon = Icons.Default.Star
                        )
                        TeamStatItem(
                            label = "Active Players",
                            value = filteredTeams.sumOf { it.playerCount }.toString(),
                            icon = Icons.Default.Person
                        )
                        TeamStatItem(
                            label = "Divisions",
                            value = filteredTeams.map { it.division }.distinct().size.toString(),
                            icon = Icons.Default.EmojiEvents
                        )
                    }
                }
            }

            // Hockey type indicator and tabs
            TabRow(selectedTabIndex = selectedTabIndex) {
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
                placeholder = { Text("Search teams...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon"
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(25.dp)
            )

            // Teams list
            if (teamListState.isLoading && filteredTeams.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (filteredTeams.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Groups,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = when (selectedTabIndex) {
                                0 -> "No teams found"
                                1 -> "You are not managing any teams"
                                2 -> "No national teams found"
                                3 -> "No club teams found"
                                else -> "No teams found"
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredTeams) { team ->
                        TeamCard(
                            team = team,
                            onTeamClick = { onNavigateToTeamDetails(team.id) },
                            onManagePlayersClick = { onNavigateToPlayerManagement(team.id) },
                            onEditClick = {
                                if (isAdmin || isCoachOrManager) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Edit team: ${team.name}")
                                    }
                                }
                            },
                            onDeleteClick = {
                                if (isAdmin) {
                                    teamToDelete = team
                                    showDeleteDialog = true
                                }
                            },
                            onMoreClick = {
                                selectedTeam = team
                                showTeamActionsMenu = true
                            },
                            canEdit = isAdmin || isCoachOrManager,
                            canDelete = isAdmin,
                            canManagePlayers = isAdmin || isCoachOrManager
                        )
                    }

                    // Add space for FAB
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog && teamToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Team") },
            text = {
                Text("Are you sure you want to delete ${teamToDelete?.name}? This will also remove all associated players and data. This action cannot be undone.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            snackbarHostState.showSnackbar("Team ${teamToDelete?.name} deleted")
                        }
                        showDeleteDialog = false
                        teamToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete Team")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    teamToDelete = null
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Team Actions Menu
    if (showTeamActionsMenu && selectedTeam != null) {
        DropdownMenu(
            expanded = showTeamActionsMenu,
            onDismissRequest = { showTeamActionsMenu = false }
        ) {
            DropdownMenuItem(
                text = { Text("View Details") },
                onClick = {
                    onNavigateToTeamDetails(selectedTeam!!.id)
                    showTeamActionsMenu = false
                },
                leadingIcon = {
                    Icon(Icons.Default.Visibility, contentDescription = null)
                }
            )

            if (isAdmin || isCoachOrManager) {
                DropdownMenuItem(
                    text = { Text("Manage Players") },
                    onClick = {
                        onNavigateToPlayerManagement(selectedTeam!!.id)
                        showTeamActionsMenu = false
                    },
                    leadingIcon = {
                        Icon(Icons.Default.PersonAdd, contentDescription = null)
                    }
                )

                DropdownMenuItem(
                    text = { Text("Edit Team") },
                    onClick = {
                        showTeamActionsMenu = false
                        scope.launch {
                            snackbarHostState.showSnackbar("Edit team: ${selectedTeam?.name}")
                        }
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Edit, contentDescription = null)
                    }
                )
            }

            if (isAdmin) {
                HorizontalDivider()
                DropdownMenuItem(
                    text = { Text("Delete Team") },
                    onClick = {
                        teamToDelete = selectedTeam
                        showDeleteDialog = true
                        showTeamActionsMenu = false
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun TeamStatItem(
    label: String,
    value: String,
    icon: ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun TeamCard(
    team: Team,
    onTeamClick: () -> Unit,
    onManagePlayersClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onMoreClick: () -> Unit,
    canEdit: Boolean,
    canDelete: Boolean,
    canManagePlayers: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTeamClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Team logo/avatar with ranking indicator
                Box {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(
                                if (team.isNationalTeam)
                                    MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.primaryContainer
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (team.isNationalTeam) Icons.Default.Star else Icons.Default.Groups,
                            contentDescription = null,
                            tint = if (team.isNationalTeam) Color.White else MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    // Ranking badge
                    if (team.ranking <= 3) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(
                                    when (team.ranking) {
                                        1 -> Color(0xFFFFD700) // Gold
                                        2 -> Color(0xFFC0C0C0) // Silver
                                        else -> Color(0xFFCD7F32) // Bronze
                                    }
                                )
                                .align(Alignment.BottomEnd),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = team.ranking.toString(),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Team info
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = team.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )

                        if (team.isNationalTeam) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                Text(
                                    text = "NATIONAL",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = "${team.category} • ${team.division}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${team.playerCount} players",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = team.homeVenue,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // More actions button
                IconButton(onClick = onMoreClick) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More actions"
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Team statistics row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TeamStatBadge(
                    label = "W",
                    value = team.wins.toString(),
                    color = MaterialTheme.colorScheme.primary
                )
                TeamStatBadge(
                    label = "D",
                    value = team.draws.toString(),
                    color = MaterialTheme.colorScheme.secondary
                )
                TeamStatBadge(
                    label = "L",
                    value = team.losses.toString(),
                    color = MaterialTheme.colorScheme.error
                )
                TeamStatBadge(
                    label = "PTS",
                    value = team.points.toString(),
                    color = MaterialTheme.colorScheme.tertiary
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            )

            // Management info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Coach: ${team.coach}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "Manager: ${team.manager}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                Row {
                    if (canManagePlayers) {
                        IconButton(
                            onClick = onManagePlayersClick,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PersonAdd,
                                contentDescription = "Manage Players",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    if (canEdit) {
                        IconButton(
                            onClick = onEditClick,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Team",
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TeamStatBadge(
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}