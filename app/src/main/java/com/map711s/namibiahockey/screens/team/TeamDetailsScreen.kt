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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.map711s.namibiahockey.data.model.Team
import com.map711s.namibiahockey.data.model.UserRole
import com.map711s.namibiahockey.viewmodel.AuthViewModel
import com.map711s.namibiahockey.viewmodel.TeamViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamDetailsScreen(
    teamId: String,
    onNavigateBack: () -> Unit,
    onNavigateToPlayerManagement: (String) -> Unit,
    viewModel: TeamDetailsViewModel = hiltViewModel(),
    teamViewModel: TeamViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val teamState by teamViewModel.teamState.collectAsState()
    val userProfileState by authViewModel.userProfileState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showJoinDialog by remember { mutableStateOf(false) }
    var hasRequestedToJoin by remember { mutableStateOf(false) }
    val playersState by viewModel.playersState.collectAsState()
    val requestsState by viewModel.requestsState.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

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
                    if (isAdmin || isCoachOrManager) {
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
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Team Header Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Team logo/avatar
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(
                                    if (team.isNationalTeam)
                                        MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.primaryContainer
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (team.isNationalTeam) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "National Team",
                                    tint = Color.White,
                                    modifier = Modifier.size(50.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Groups,
                                    contentDescription = "Team",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(50.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = team.name,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )

                        if (team.isNationalTeam) {
                            Surface(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Text(
                                    text = "NATIONAL TEAM",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "${team.category} • ${team.division}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )

                        if (team.description.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = team.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Team Information Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Team Information",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Coach
                        if (team.coach.isNotEmpty()) {
                            TeamInfoRow(
                                icon = Icons.Default.Person,
                                label = "Coach",
                                value = team.coach
                            )
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        }

                        // Manager
                        if (team.manager.isNotEmpty()) {
                            TeamInfoRow(
                                icon = Icons.Default.Settings,
                                label = "Manager",
                                value = team.manager
                            )
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        }

                        // Players count
                        TeamInfoRow(
                            icon = Icons.Default.Groups,
                            label = "Players",
                            value = "${team.players.size} registered"
                        )

                        if (team.establishedYear > 0) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                            TeamInfoRow(
                                icon = Icons.Default.CalendarMonth,
                                label = "Established",
                                value = team.establishedYear.toString()
                            )
                        }

                        if (team.homeVenue.isNotEmpty()) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                            TeamInfoRow(
                                icon = Icons.Default.LocationOn,
                                label = "Home Venue",
                                value = team.homeVenue
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action buttons based on user role
                when {
                    isAdmin || isCoachOrManager -> {
                        // Admin/Coach/Manager actions
                        Button(
                            onClick = { onNavigateToPlayerManagement(teamId) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Manage Players"
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Manage Players")
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedButton(
                            onClick = { /* Navigate to team settings */ },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Team Settings"
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Team Settings")
                        }
                    }

                    isPlayer -> {
                        // Player actions
                        val isAlreadyMember = team.players.contains(currentUser?.id ?: "")

                        if (isAlreadyMember) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Groups,
                                        contentDescription = "Member",
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "You are a member of this team",
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        } else if (hasRequestedToJoin) {
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PersonAdd,
                                        contentDescription = "Request Sent",
                                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Join request sent",
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        } else {
                            Button(
                                onClick = {
                                    showJoinDialog = true
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PersonAdd,
                                    contentDescription = "Request to Join"
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Request to Join Team")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // Join team request dialog would be implemented here
    // if (showJoinDialog) { ... }


    TeamPlayersSection(
        players = playersState.players,
        canManage = userProfile?.role in listOf(UserRole.COACH, UserRole.MANAGER, UserRole.ADMIN),
        onRemovePlayer = { playerId ->
            viewModel.removePlayerFromTeam(playerId)
        }
    )

    // Pending requests section (visible to coaches/managers/admins)
    if (userProfile?.role in listOf(UserRole.COACH, UserRole.MANAGER, UserRole.ADMIN)) {
        PendingRequestsSection(
            requests = requestsState.requests,
            onApproveRequest = { requestId ->
                viewModel.approveRequest(requestId)
            },
            onRejectRequest = { requestId ->
                viewModel.rejectRequest(requestId)
            }
        )
    }
}

@Composable
private fun TeamInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}