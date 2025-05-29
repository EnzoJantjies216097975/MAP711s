package com.map711s.namibiahockey.screens.livegame

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.map711s.namibiahockey.data.model.GameEventType
import com.map711s.namibiahockey.data.model.LiveGame
import com.map711s.namibiahockey.viewmodel.LiveGameViewModel
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveGameManagementScreen(
    onNavigateBack: () -> Unit,
    viewModel: LiveGameViewModel = hiltViewModel()
) {
    val liveGames by viewModel.liveGames.collectAsState()
    val gameState by viewModel.gameState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showCreateGameDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Live Game Management") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateGameDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create Live Game",
                    tint = Color.White
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(liveGames) { game ->
                LiveGameCard(
                    game = game,
                    onUpdateScore = { team1Score, team2Score ->
                        viewModel.updateScore(game.id, team1Score, team2Score)
                    },
                    onAddEvent = { eventType, description ->
                        viewModel.addGameEvent(game.id, eventType, description)
                    },
                    onEndGame = {
                        viewModel.endGame(game.id)
                    }
                )
            }
        }
    }

    if (showCreateGameDialog) {
        CreateLiveGameDialog(
            onDismiss = { showCreateGameDialog = false },
            onCreateGame = { game ->
                viewModel.createLiveGame(game)
                showCreateGameDialog = false
            }
        )
    }
}

@Composable
fun LiveGameCard(
    game: LiveGame,
    onUpdateScore: (Int, Int) -> Unit,
    onAddEvent: (GameEventType, String) -> Unit,
    onEndGame: () -> Unit
) {
    var team1Score by remember { mutableStateOf(game.team1Score.toString()) }
    var team2Score by remember { mutableStateOf(game.team2Score.toString()) }
    var showEventDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Game header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(game.team1Name, fontWeight = FontWeight.Bold)
                Text("VS", style = MaterialTheme.typography.bodySmall)
                Text(game.team2Name, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Score inputs (admin only)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedTextField(
                    value = team1Score,
                    onValueChange = { team1Score = it },
                    label = { Text("${game.team1Name} Score") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedTextField(
                    value = team2Score,
                    onValueChange = { team2Score = it },
                    label = { Text("${game.team2Name} Score") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        val score1 = team1Score.toIntOrNull() ?: 0
                        val score2 = team2Score.toIntOrNull() ?: 0
                        onUpdateScore(score1, score2)
                    }
                ) {
                    Text("Update Score")
                }

                Button(onClick = { showEventDialog = true }) {
                    Text("Add Event")
                }

                Button(
                    onClick = onEndGame,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("End Game")
                }
            }
        }
    }

    if (showEventDialog) {
        AddGameEventDialog(
            onDismiss = { showEventDialog = false },
            onEventAdded = { eventType, description ->
                onAddEvent(eventType, description)
                showEventDialog = false
            }
        )
    }
}

@Composable
fun CreateLiveGameDialog(
    onDismiss: () -> Unit,
    onCreateGame: (LiveGame) -> Unit
) {
    var team1Name by remember { mutableStateOf("") }
    var team2Name by remember { mutableStateOf("") }
    var venue by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Live Game") },
        text = {
            Column {
                OutlinedTextField(
                    value = team1Name,
                    onValueChange = { team1Name = it },
                    label = { Text("Team 1 Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = team2Name,
                    onValueChange = { team2Name = it },
                    label = { Text("Team 2 Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = venue,
                    onValueChange = { venue = it },
                    label = { Text("Venue") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val game = LiveGame(
                        team1Name = team1Name,
                        team2Name = team2Name,
                        venue = venue,
                        startTime = Date(),
                        isLive = true
                    )
                    onCreateGame(game)
                },
                enabled = team1Name.isNotBlank() && team2Name.isNotBlank() && venue.isNotBlank()
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AddGameEventDialog(
    onDismiss: () -> Unit,
    onEventAdded: (GameEventType, String) -> Unit
) {
    var selectedEventType by remember { mutableStateOf(GameEventType.GOAL) }
    var eventDescription by remember { mutableStateOf("") }
    var showEventTypeMenu by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Game Event") },
        text = {
            Column {
                // Event type dropdown
                OutlinedTextField(
                    value = selectedEventType.name.replace("_", " "),
                    onValueChange = { },
                    label = { Text("Event Type") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showEventTypeMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Select Event Type"
                            )
                        }
                    }
                )

                DropdownMenu(
                    expanded = showEventTypeMenu,
                    onDismissRequest = { showEventTypeMenu = false }
                ) {
                    GameEventType.entries.forEach { eventType ->
                        DropdownMenuItem(
                            text = { Text(eventType.name.replace("_", " ")) },
                            onClick = {
                                selectedEventType = eventType
                                showEventTypeMenu = false
                            },
                            trailingIcon = {
                                if (selectedEventType == eventType) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected"
                                    )
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = eventDescription,
                    onValueChange = { eventDescription = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onEventAdded(selectedEventType, eventDescription)
                },
                enabled = eventDescription.isNotBlank()
            ) {
                Text("Add Event")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}