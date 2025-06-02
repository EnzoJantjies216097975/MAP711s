package com.map711s.namibiahockey.screens.team

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.map711s.namibiahockey.data.model.Player
import com.map711s.namibiahockey.data.model.PlayerRequest
import com.map711s.namibiahockey.data.model.StatItemDisplay
import com.map711s.namibiahockey.data.model.Team
import com.map711s.namibiahockey.data.model.TeamStatistics
import com.map711s.namibiahockey.data.model.User
import com.map711s.namibiahockey.data.model.UserRole
import com.map711s.namibiahockey.viewmodel.AuthViewModel
import com.map711s.namibiahockey.viewmodel.PlayerViewModel
import com.map711s.namibiahockey.viewmodel.TeamDetailsViewModel
import com.map711s.namibiahockey.viewmodel.TeamViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamDetailsScreen(
    teamId: String,
    onNavigateBack: () -> Unit,
    onNavigateToPlayerManagement: (String) -> Unit,
    viewModel: TeamDetailsViewModel = hiltViewModel(),
    teamViewModel: TeamViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    playerViewModel: PlayerViewModel = hiltViewModel(),
) {
    val teamState by teamViewModel.teamState.collectAsState()
    val userProfileState by authViewModel.userProfileState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showJoinDialog by remember { mutableStateOf(false) }
    var hasRequestedToJoin by remember { mutableStateOf(false) }
    val playersState by viewModel.playersState.collectAsState()
    val requestsState by viewModel.requestsState.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    // FIXED: Get team stats from viewModel
    val teamStats by viewModel.teamStats.collectAsState()

    val playerListState by playerViewModel.playerListState.collectAsState()

    val scope = rememberCoroutineScope()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Overview", "Players", "Statistics", "Requests")

    var showRemovePlayerDialog by remember { mutableStateOf(false) }
    var playerToRemove by remember { mutableStateOf<Player?>(null) }
    var showAddPlayerDialog by remember { mutableStateOf(false) }

    // Load team details when screen is displayed
    LaunchedEffect(teamId) {
        if (teamId.startsWith("national_")) {
            // Load national team data
            val nationalTeam = Team.getNationalTeams().find { it.id == teamId }
            // You would need to add this method to TeamViewModel
            // teamViewModel.setTeam(nationalTeam)
        } else {
            Log.d("TAG", teamId)
            teamViewModel.getTeam(teamId)
        }
    }

    LaunchedEffect(teamId) {
        viewModel.loadTeamDetails(teamId)
        viewModel.loadTeamPlayers(teamId)
        if (userProfile?.role in listOf(UserRole.COACH, UserRole.MANAGER, UserRole.ADMIN)) {
            viewModel.loadPendingRequests(teamId)
        }
    }

    val team = teamState.team
    val currentUser = userProfileState.user
    val isAdmin = currentUser?.role == UserRole.ADMIN
    val isCoachOrManager = currentUser?.role == UserRole.COACH || currentUser?.role == UserRole.MANAGER
    val isPlayer = currentUser?.role == UserRole.PLAYER
    val players = playerListState.players
    val isCoach = currentUser?.role == UserRole.COACH
    val isManager = currentUser?.role == UserRole.MANAGER
    val canManagePlayers = isAdmin || isCoach

    // FIXED: Get pending requests from requestsState
    val pendingRequests = requestsState

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Team Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (canManagePlayers) {
                        IconButton(onClick = { /* Navigate to edit team */ }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Team"
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (canManagePlayers && selectedTabIndex == 1) {
                FloatingActionButton(
                    onClick = { showAddPlayerDialog = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = "Add Player"
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        if (teamState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (team == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Team not found")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Team Header Card - FIXED: Use proper teamStats
                TeamHeaderCard(
                    team = team,
                    teamStats = teamStats ?: TeamStatistics() // Provide default if null
                )

                // Tab Navigation
                TabRow(selectedTabIndex = selectedTabIndex) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(title) }
                        )
                    }
                }

                // Tab Content
                when (selectedTabIndex) {
                    0 -> TeamOverviewTab(
                        team = team,
                        teamStats = teamStats ?: TeamStatistics(), // FIXED
                        currentUser = currentUser,
                        onJoinTeam = { showJoinDialog = true }
                    )
                    1 -> TeamPlayersTab(
                        players = players,
                        canManagePlayers = canManagePlayers,
                        onRemovePlayer = { player ->
                            playerToRemove = player
                            showRemovePlayerDialog = true
                        }
                    )
                    2 -> TeamStatisticsTab(teamStats = teamStats ?: TeamStatistics()) // FIXED
                    3 -> if (canManagePlayers) {
                        TeamRequestsTab(
                            requests = pendingRequests, // FIXED
                            onApproveRequest = { requestId ->
                                viewModel.approveRequest(requestId)
                                scope.launch {
                                    snackbarHostState.showSnackbar("Request approved")
                                }
                            },
                            onRejectRequest = { requestId ->
                                viewModel.rejectRequest(requestId)
                                scope.launch {
                                    snackbarHostState.showSnackbar("Request rejected")
                                }
                            }
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Access denied")
                        }
                    }
                }
            }
        }
    }

    // Rest of the dialogs remain the same...
    // Join Team Dialog
    if (showJoinDialog) {
        AlertDialog(
            onDismissRequest = { showJoinDialog = false },
            title = { Text("Join Team") },
            text = { Text("Would you like to send a request to join ${team?.name}?") },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            snackbarHostState.showSnackbar("Join request sent!")
                        }
                        showJoinDialog = false
                    }
                ) {
                    Text("Send Request")
                }
            },
            dismissButton = {
                TextButton(onClick = { showJoinDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Remove Player Dialog
    if (showRemovePlayerDialog && playerToRemove != null) {
        AlertDialog(
            onDismissRequest = { showRemovePlayerDialog = false },
            title = { Text("Remove Player") },
            text = { Text("Are you sure you want to remove ${playerToRemove?.name} from the team?") },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            snackbarHostState.showSnackbar("${playerToRemove?.name} removed from team")
                        }
                        showRemovePlayerDialog = false
                        playerToRemove = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Remove")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showRemovePlayerDialog = false
                    playerToRemove = null
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun TeamHeaderCard(
    team: Team,
    teamStats: TeamStatistics
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Team logo placeholder
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Groups,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = team.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${team.category} • ${team.division}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Team stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItemDisplay("Games", teamStats.gamesPlayed.toString())
                StatItemDisplay("Wins", teamStats.wins.toString())
                StatItemDisplay("Draws", teamStats.draws.toString())
                StatItemDisplay("Losses", teamStats.losses.toString())
            }
        }
    }
}

@Composable
fun TeamOverviewTab(
    team: Team,
    teamStats: TeamStatistics,
    currentUser: User?,
    onJoinTeam: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Team Information",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "Coach: ${team.coach}")
                    Text(text = "Manager: ${team.manager}")
                    Text(text = "Founded: ${team.establishedYear}")
                    Text(text = "Home Venue: ${team.homeVenue}")

                    if (team.description.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Description",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(text = team.description)
                    }
                }
            }
        }

        // Join team button for players
        if (currentUser?.role == UserRole.PLAYER) {
            item {
                Button(
                    onClick = onJoinTeam,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Request to Join Team")
                }
            }
        }
    }
}

@Composable
fun TeamPlayersTab(
    players: List<Player>,
    canManagePlayers: Boolean,
    onRemovePlayer: (Player) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (players.isEmpty()) {
            item {
                Text(
                    text = "No players in this team",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        } else {
            items(players) { player ->
                Card {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Player avatar
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = player.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${player.position} • #${player.jerseyNumber}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }

                        if (canManagePlayers) {
                            IconButton(onClick = { onRemovePlayer(player) }) {
                                Icon(
                                    imageVector = Icons.Default.PersonRemove,
                                    contentDescription = "Remove Player",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TeamStatisticsTab(
    teamStats: TeamStatistics
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Season Statistics",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatItemDisplay("Points", teamStats.points.toString())
                        StatItemDisplay("Goals For", teamStats.goalsFor.toString())
                        StatItemDisplay("Goals Against", teamStats.goalsAgainst.toString())
                        StatItemDisplay("Goal Diff", teamStats.getGoalDifference().toString())
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = "Win Percentage: ${String.format("%.1f", teamStats.getWinPercentage())}%")
                    Text(text = "Current Form: ${teamStats.getFormString()}")
                }
            }
        }
    }
}

@Composable
fun TeamRequestsTab(
    requests: List<PlayerRequest>,
    onApproveRequest: (String) -> Unit,
    onRejectRequest: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (requests.isEmpty()) {
            item {
                Text(
                    text = "No pending requests",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        } else {
            items(requests) { request ->
                Card {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = request.playerName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        if (request.message.isNotEmpty()) {
                            Text(
                                text = request.message,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            OutlinedButton(
                                onClick = { onRejectRequest(request.id) }
                            ) {
                                Text("Reject")
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = { onApproveRequest(request.id) }
                            ) {
                                Text("Approve")
                            }
                        }
                    }
                }
            }
        }
    }
}

