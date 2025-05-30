package com.map711s.namibiahockey.screens.team

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.map711s.namibiahockey.data.model.Player
import com.map711s.namibiahockey.data.model.PlayerRequest
import com.map711s.namibiahockey.data.model.StatItem
import com.map711s.namibiahockey.data.model.Team
import com.map711s.namibiahockey.data.model.TeamStatistics
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