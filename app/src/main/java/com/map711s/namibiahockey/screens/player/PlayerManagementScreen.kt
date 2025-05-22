package com.map711s.namibiahockey.screens.player

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.map711s.namibiahockey.data.model.HockeyType
import com.map711s.namibiahockey.data.model.PlayerListItem
import com.map711s.namibiahockey.data.model.UserRole
import com.map711s.namibiahockey.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerManagementScreen(
    onNavigateBack: () -> Unit,
    hockeyType: HockeyType,
    onNavigateToPlayerDetails: (String) -> Unit = {},
    authViewModel: AuthViewModel = hiltViewModel()
){
    var searchQuery by remember { mutableStateOf("") }
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("All Players", "My Team", "National Teams", "Free Agents")

    val userProfileState by authViewModel.userProfileState.collectAsState()
    val currentUser = userProfileState.user
    val isAdmin = currentUser?.role == UserRole.ADMIN
    val isCoachOrManager = currentUser?.role == UserRole.COACH || currentUser?.role == UserRole.MANAGER

    // Dialog states
    var showAddPlayerDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var playerToDelete by remember { mutableStateOf<PlayerListItem?>(null) }
    var showPlayerDetailsDialog by remember { mutableStateOf(false) }
    var selectedPlayer by remember { mutableStateOf<PlayerListItem?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()


    // Enhanced sample players with more details
    val players = remember {
        listOf(
            PlayerListItem(
                id = "1",
                name = "John Doe",
                position = "Forward",
                teamName = "Windhoek Warriors",
                jerseyNumber = 10,
                age = 25,
                nationality = "Namibian",
                isNationalPlayer = true,
                hockeyType = HockeyType.OUTDOOR,
                contactEmail = "john.doe@email.com",
                contactPhone = "+264 81 123 4567",
                experienceYears = 8,
                rating = 4.5f
            ),
            PlayerListItem(
                id = "2",
                name = "Jane Smith",
                position = "Goalkeeper",
                teamName = "Windhoek Warriors",
                jerseyNumber = 1,
                age = 23,
                nationality = "Namibian",
                isNationalPlayer = true,
                hockeyType = HockeyType.OUTDOOR,
                contactEmail = "jane.smith@email.com",
                contactPhone = "+264 81 234 5678",
                experienceYears = 6,
                rating = 4.8f
            ),
            PlayerListItem(
                id = "3",
                name = "Michael Brown",
                position = "Defender",
                teamName = "Okahandja Ostriches",
                jerseyNumber = 4,
                age = 28,
                nationality = "Namibian",
                isNationalPlayer = false,
                hockeyType = HockeyType.OUTDOOR,
                contactEmail = "michael.brown@email.com",
                contactPhone = "+264 81 345 6789",
                experienceYears = 10,
                rating = 4.2f
            ),
            PlayerListItem(
                id = "4",
                name = "Sarah Johnson",
                position = "Midfielder",
                teamName = "Swakopmund Seagulls",
                jerseyNumber = 7,
                age = 26,
                nationality = "Namibian",
                isNationalPlayer = true,
                hockeyType = HockeyType.INDOOR,
                contactEmail = "sarah.johnson@email.com",
                contactPhone = "+264 81 456 7890",
                experienceYears = 7,
                rating = 4.6f
            ),
            PlayerListItem(
                id = "5",
                name = "David Wilson",
                position = "Forward",
                teamName = "",
                jerseyNumber = 0,
                age = 22,
                nationality = "Namibian",
                isNationalPlayer = false,
                hockeyType = HockeyType.OUTDOOR,
                contactEmail = "david.wilson@email.com",
                contactPhone = "+264 81 567 8901",
                experienceYears = 3,
                rating = 3.8f
            )
        )
    }

    // Filter players based on search and tab
    val filteredPlayers = if (searchQuery.isBlank()) {
        when (selectedTabIndex) {
            0 -> players.filter { it.hockeyType == hockeyType || hockeyType == HockeyType.BOTH }
            1 -> players.filter { it.teamName == "Windhoek Warriors" } // Mock user's team
            2 -> players.filter { it.isNationalPlayer }
            3 -> players.filter { it.teamName.isEmpty() } // Free agents
            else -> players
        }
    } else {
        players.filter {
            (it.name.contains(searchQuery, ignoreCase = true) ||
                    it.position.contains(searchQuery, ignoreCase = true) ||
                    it.teamName.contains(searchQuery, ignoreCase = true) ||
                    it.jerseyNumber.toString().contains(searchQuery)) &&
                    (it.hockeyType == hockeyType || hockeyType == HockeyType.BOTH)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Player Management")
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
                    onClick = { showAddPlayerDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Player",
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
            // Enhanced header with statistics
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    PlayerStatItem(
                        label = "Total Players",
                        value = filteredPlayers.size.toString(),
                        icon = Icons.Default.Person
                    )
                    PlayerStatItem(
                        label = "National Players",
                        value = filteredPlayers.count { it.isNationalPlayer }.toString(),
                        icon = Icons.Default.Star
                    )
                    PlayerStatItem(
                        label = "Free Agents",
                        value = filteredPlayers.count { it.teamName.isEmpty() }.toString(),
                        icon = Icons.Default.PersonAdd
                    )
                }
            }

            // Tabs for filtering players
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
                placeholder = { Text("Search players...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon"
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(25.dp)
            )

            // Player list
            if (filteredPlayers.isEmpty()) {
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
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = when (selectedTabIndex) {
                                0 -> "No players found"
                                1 -> "No team players found"
                                2 -> "No national players found"
                                3 -> "No free agents found"
                                else -> "No players found"
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredPlayers) { player ->
                        PlayerCard(
                            player = player,
                            onEditClick = {
                                if (isAdmin || isCoachOrManager) {
                                    // Handle edit
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Edit player: ${player.name}")
                                    }
                                }
                            },
                            onDeleteClick = {
                                if (isAdmin) {
                                    playerToDelete = player
                                    showDeleteDialog = true
                                }
                            },
                            onViewClick = {
                                selectedPlayer = player
                                showPlayerDetailsDialog = true
                            },
                            canEdit = isAdmin || isCoachOrManager,
                            canDelete = isAdmin
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

    // Add Player Dialog
    if (showAddPlayerDialog) {
        AddPlayerDialog(
            onDismiss = { showAddPlayerDialog = false },
            onConfirm = { playerData ->
                // Handle adding player
                scope.launch {
                    snackbarHostState.showSnackbar("Player ${playerData["name"]} added successfully!")
                }
                showAddPlayerDialog = false
            },
            hockeyType = hockeyType
        )
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog && playerToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Player") },
            text = { Text("Are you sure you want to delete ${playerToDelete?.name}? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            snackbarHostState.showSnackbar("Player ${playerToDelete?.name} deleted")
                        }
                        showDeleteDialog = false
                        playerToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    playerToDelete = null
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Player Details Dialog
    if (showPlayerDetailsDialog && selectedPlayer != null) {
        PlayerDetailsDialog(
            player = selectedPlayer!!,
            onDismiss = {
                showPlayerDetailsDialog = false
                selectedPlayer = null
            },
            onNavigateToFullDetails = {
                onNavigateToPlayerDetails(selectedPlayer!!.id)
                showPlayerDetailsDialog = false
                selectedPlayer = null
            }
        )
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

@Composable
private fun PlayerStatItem(
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
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
        )
    }
}

@Composable
private fun AddPlayerDialog(
    onDismiss: () -> Unit,
    onConfirm: (Map<String, String>) -> Unit,
    hockeyType: HockeyType
) {
    var name by remember { mutableStateOf("") }
    var position by remember { mutableStateOf("") }
    var positionExpanded by remember { mutableStateOf(false) }
    var jerseyNumber by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    val positions = listOf("Goalkeeper", "Defender", "Midfielder", "Forward")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Add New Player",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Name field
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Position dropdown
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = position,
                        onValueChange = { },
                        label = { Text("Position") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { positionExpanded = true }) {
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Select Position"
                                )
                            }
                        }
                    )

                    DropdownMenu(
                        expanded = positionExpanded,
                        onDismissRequest = { positionExpanded = false }
                    ) {
                        positions.forEach { pos ->
                            DropdownMenuItem(
                                text = { Text(pos) },
                                onClick = {
                                    position = pos
                                    positionExpanded = false
                                },
                                trailingIcon = {
                                    if (position == pos) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected"
                                        )
                                    }
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row {
                    OutlinedTextField(
                        value = jerseyNumber,
                        onValueChange = { jerseyNumber = it },
                        label = { Text("Jersey #") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedTextField(
                        value = age,
                        onValueChange = { age = it },
                        label = { Text("Age") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Done
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            val playerData = mapOf(
                                "name" to name,
                                "position" to position,
                                "jerseyNumber" to jerseyNumber,
                                "age" to age,
                                "email" to email,
                                "phone" to phone,
                                "hockeyType" to hockeyType.name
                            )
                            onConfirm(playerData)
                        },
                        enabled = name.isNotBlank() && position.isNotBlank()
                    ) {
                        Text("Add Player")
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayerCard(
    player: PlayerListItem,
    onEditClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    onViewClick: (String) -> Unit,
    canEdit: Boolean,
    canDelete: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onViewClick(player.id) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Player avatar/icon with status indicator
                Box {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(
                                if (player.isNationalPlayer)
                                    MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.material.Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = if (player.isNationalPlayer) Color.White else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    // National player indicator
                    if (player.isNationalPlayer) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.secondary)
                                .align(Alignment.BottomEnd),
                            contentAlignment = Alignment.Center
                        ) {
                            androidx.compose.material.Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "National Player",
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Player info
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = player.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        if (player.teamName.isEmpty()) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.tertiaryContainer,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "FREE",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = "${player.position} | #${if (player.jerseyNumber > 0) player.jerseyNumber else "N/A"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    Text(
                        text = if (player.teamName.isNotEmpty()) player.teamName else "No Team",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )

                    // Rating stars
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        repeat(5) { index ->
                            androidx.compose.material.Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = if (index < player.rating.toInt())
                                    MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${player.rating}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }

                // Action buttons
                Column {
                    androidx.compose.material.IconButton(
                        onClick = { onViewClick(player.id) }
                    ) {
                        androidx.compose.material.Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = "View Player",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    if (canEdit) {
                        androidx.compose.material.IconButton(
                            onClick = { onEditClick(player.id) }
                        ) {
                            androidx.compose.material.Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Player",
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }

                    if (canDelete) {
                        androidx.compose.material.IconButton(
                            onClick = { onDeleteClick(player.id) }
                        ) {
                            androidx.compose.material.Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Player",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            )

            // Additional info row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Age: ${player.age}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = "Experience: ${player.experienceYears} years",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = player.hockeyType.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun PlayerDetailsDialog(
    player: PlayerListItem,
    onDismiss: () -> Unit,
    onNavigateToFullDetails: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = player.name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${player.position} | #${if (player.jerseyNumber > 0) player.jerseyNumber else "N/A"}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        if (player.isNationalPlayer) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Text(
                                    text = "NATIONAL PLAYER",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Quick info
                Column {
                    DetailRow("Team", if (player.teamName.isNotEmpty()) player.teamName else "No Team")
                    DetailRow("Age", "${player.age} years")
                    DetailRow("Experience", "${player.experienceYears} years")
                    DetailRow("Hockey Type", player.hockeyType.name)
                    DetailRow("Contact", player.contactEmail)
                    DetailRow("Phone", player.contactPhone)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Close")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(onClick = onNavigateToFullDetails) {
                        Text("View Full Profile")
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}