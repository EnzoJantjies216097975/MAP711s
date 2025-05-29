package com.map711s.namibiahockey.screens.livegame

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.map711s.namibiahockey.data.model.GameEventType
import com.map711s.namibiahockey.data.model.LiveGame

@Composable
fun LiveGameManagementScreen(
    onNavigateBack: () -> Unit,
    viewModel: LiveGameViewModel = hiltViewModel()
) {
    val liveGames by viewModel.liveGames.collectAsState()
    val gameState by viewModel.gameState.collectAsState()

    LazyColumn {
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
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