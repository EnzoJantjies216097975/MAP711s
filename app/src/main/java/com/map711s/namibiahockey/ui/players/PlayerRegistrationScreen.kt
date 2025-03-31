package com.map711s.namibiahockey.ui.players

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerRegistrationScreen(
    onNavigateBack: () -> Unit,
    onPlayerRegistered: () -> Unit
) {
    var playerName by remember { mutableStateOf("") }
    var position by remember { mutableStateOf("") }
    var jerseyNumber by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var selectedTeam by remember { mutableStateOf("") }
    var selectedTeamId by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()

    val positions = listOf("Forward", "Midfielder", "Defender", "Goalkeeper")
    var showPositionsMenu by remember { mutableStateOf(false) }

    // User's teams for dropdown
    val userTeams = getSampleTeams().filter { it.isUserTeam }
    var showTeamsMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Register Player") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    IconButton(
                        onClick = {
                            // Form validation
                            when {
                                playerName.isBlank() -> {
                                    errorMessage = "Player name is required"
                                }
                                position.isBlank() -> {
                                    errorMessage = "Position is required"
                                }
                                jerseyNumber.isBlank() -> {
                                    errorMessage = "Jersey number is required"
                                }
                                jerseyNumber.toIntOrNull() == null -> {
                                    errorMessage = "Jersey number must be a valid number"
                                }
                                selectedTeamId.isBlank() -> {
                                    errorMessage = "Please select a team"
                                }
                                dateOfBirth.isBlank() -> {
                                    errorMessage = "Date of birth is required"
                                }
                                else -> {
                                    errorMessage = null
                                    isLoading = true

                                    // Simulate API call
                                    android.os.Handler().postDelayed({
                                        isLoading = false
                                        onPlayerRegistered()
                                    }, 1500)
                                }
                            }
                        }
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Save",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Player Photo
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                // Placeholder image
                Card(
                    modifier = Modifier.fillMaxSize(),
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Camera,
                            contentDescription = "Upload Player Photo",
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Text(
                text = "Upload Player Photo",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
            )

            // Error message
            errorMessage?.let {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            // Player Name
            OutlinedTextField(
                value = playerName,
                onValueChange = { playerName = it; errorMessage = null },
                label = { Text("Player Name *") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Team Selection
            ExposedDropdownMenuBox(
                expanded = showTeamsMenu,
                onExpandedChange = { showTeamsMenu = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedTeam,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Team *") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = showTeamsMenu)
                    },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = showTeamsMenu,
                    onDismissRequest = { showTeamsMenu = false }
                ) {
                    userTeams.forEach { team ->
                        DropdownMenuItem(
                            text = { Text(team.name) },
                            onClick = {
                                selectedTeam = team.name
                                selectedTeamId = team.id
                                showTeamsMenu = false
                                errorMessage = null
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Position
            ExposedDropdownMenuBox(
                expanded = showPositionsMenu,
                onExpandedChange = { showPositionsMenu = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = position,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Position *") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = showPositionsMenu)
                    },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = showPositionsMenu,
                    onDismissRequest = { showPositionsMenu = false }
                ) {
                    positions.forEach { pos ->
                        DropdownMenuItem(
                            text = { Text(pos) },
                            onClick = {
                                position = pos
                                showPositionsMenu = false
                                errorMessage = null
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Jersey Number
            OutlinedTextField(
                value = jerseyNumber,
                onValueChange = { jerseyNumber = it; errorMessage = null },
                label = { Text("Jersey Number *") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Date of Birth
            OutlinedTextField(
                value = dateOfBirth,
                onValueChange = { dateOfBirth = it; errorMessage = null },
                label = { Text("Date of Birth (DD/MM/YYYY) *") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Submit Button
            Button(
                onClick = {
                    // Form validation (same as action button)
                    when {
                        playerName.isBlank() -> {
                            errorMessage = "Player name is required"
                        }
                        position.isBlank() -> {
                            errorMessage = "Position is required"
                        }
                        jerseyNumber.isBlank() -> {
                            errorMessage = "Jersey number is required"
                        }
                        jerseyNumber.toIntOrNull() == null -> {
                            errorMessage = "Jersey number must be a valid number"
                        }
                        selectedTeamId.isBlank() -> {
                            errorMessage = "Please select a team"
                        }
                        dateOfBirth.isBlank() -> {
                            errorMessage = "Date of birth is required"
                        }
                        else -> {
                            errorMessage = null
                            isLoading = true

                            // Simulate API call
                            android.os.Handler().postDelayed({
                                isLoading = false
                                onPlayerRegistered()
                            }, 1500)
                        }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text("Register Player")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "* Required fields",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}