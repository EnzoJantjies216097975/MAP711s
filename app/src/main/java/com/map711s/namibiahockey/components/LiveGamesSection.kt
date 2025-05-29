package com.map711s.namibiahockey.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.map711s.namibiahockey.data.model.GameEventType
import com.map711s.namibiahockey.data.model.LiveGame
import com.map711s.namibiahockey.data.model.UserRole
import com.map711s.namibiahockey.viewmodel.AuthViewModel
import com.map711s.namibiahockey.viewmodel.LiveGameViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@Composable
fun LiveGamesSection(
    modifier: Modifier = Modifier,
    liveGameViewModel: LiveGameViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val liveGames by liveGameViewModel.liveGames.collectAsState()
    val userProfileState by authViewModel.userProfileState.collectAsState()
    val currentUser = userProfileState.user
    val canManage = currentUser?.role == UserRole.ADMIN

    // Only show if there are live games
    if (liveGames.isNotEmpty()) {
        Card(
            modifier = modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.SportsSoccer,
                        contentDescription = "Live Games",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "🔴 LIVE GAMES",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.weight(1f))

                    // Live indicator
                    LiveIndicator()
                }

                Spacer(modifier = Modifier.height(16.dp))

                liveGames.forEach { game ->
                    LiveGameCard(
                        game = game,
                        canManage = canManage,
                        onScoreUpdate = { gameId, team1Score, team2Score ->
                            if (canManage) {
                                liveGameViewModel.updateScore(gameId, team1Score, team2Score)
                            }
                        },
                        onAddEvent = { gameId, eventType, description ->
                            if (canManage) {
                                liveGameViewModel.addGameEvent(gameId, eventType, description)
                            }
                        }
                    )

                    if (game != liveGames.last()) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LiveGameCard(
    game: LiveGame,
    canManage: Boolean,
    onScoreUpdate: (String, Int, Int) -> Unit,
    onAddEvent: (String, GameEventType, String) -> Unit
) {
    var elapsedTime by remember { mutableStateOf("") }

    // Update elapsed time every minute
    LaunchedEffect(game.startTime) {
        while (true) {
            val now = Date()
            val diffInMillis = now.time - game.startTime.time
            val minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis)
            elapsedTime = "${minutes}'"
            delay(60000) // Update every minute
        }
    }

    Column {
        // Match Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = game.team1Name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "VS",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = elapsedTime,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = game.team2Name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Score Display
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ScoreBox(
                score = game.team1Score,
                teamName = game.team1Name,
                isHigher = game.team1Score > game.team2Score
            )

            Text(
                text = "-",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            ScoreBox(
                score = game.team2Score,
                teamName = game.team2Name,
                isHigher = game.team2Score > game.team1Score
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Game Details
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Venue",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = game.venue,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = "Start Time",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(game.startTime),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }

        // Recent Events
        if (game.events.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            val recentEvents = game.events.takeLast(2)

            Text(
                text = "Recent Events:",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )

            recentEvents.forEach { event ->
                Text(
                    text = "⚡ ${event.description}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    modifier = Modifier.padding(start = 8.dp, top = 2.dp)
                )
            }
        }

        // Admin Controls (if user is admin)
        if (canManage) {
            Spacer(modifier = Modifier.height(12.dp))
            AdminGameControls(
                game = game,
                onScoreUpdate = onScoreUpdate,
                onAddEvent = onAddEvent
            )
        }
    }
}

@Composable
fun ScoreBox(
    score: Int,
    teamName: String,
    isHigher: Boolean
) {
    Surface(
        color = if (isHigher) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.padding(4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = score.toString(),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = if (isHigher) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun LiveIndicator() {
    var isVisible by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            isVisible = !isVisible
        }
    }

    Box(
        modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(
                if (isVisible) MaterialTheme.colorScheme.error else Color.Transparent
            )
    )
}

@Composable
fun AdminGameControls(
    game: LiveGame,
    onScoreUpdate: (String, Int, Int) -> Unit,
    onAddEvent: (String, GameEventType, String) -> Unit
) {
    var showScoreDialog by remember { mutableStateOf(false) }
    var showEventDialog by remember { mutableStateOf(false) }

    Surface(
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = "Admin Controls",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                androidx.compose.material3.OutlinedButton(
                    onClick = { showScoreDialog = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Update Score", fontSize = 12.sp)
                }

                androidx.compose.material3.OutlinedButton(
                    onClick = { showEventDialog = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Add Event", fontSize = 12.sp)
                }
            }
        }
    }

    // Score update dialog would be implemented here
    // Event addition dialog would be implemented here
}