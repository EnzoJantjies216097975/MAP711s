package com.map711s.namibiahockey.components

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Sports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.map711s.namibiahockey.data.model.GameResult
import com.map711s.namibiahockey.data.model.StatItem
import com.map711s.namibiahockey.data.model.TeamSeasonStats

@Composable
fun GameResultsDisplay(
    gameResults: List<GameResult>,
    teamStats: List<TeamSeasonStats> = emptyList(),
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Match Results", "Team Standings", "Statistics")

    Column(modifier = modifier) {
        if (gameResults.isNotEmpty() || teamStats.isNotEmpty()) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTabIndex) {
                0 -> MatchResultsTab(gameResults = gameResults)
                1 -> TeamStandingsTab(teamStats = teamStats)
                2 -> StatisticsTab(gameResults = gameResults)
            }
        } else {
            // No results available yet
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Sports,
                        contentDescription = "No Results",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Results Coming Soon",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Match results and statistics will be available after the event concludes.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun MatchResultsTab(gameResults: List<GameResult>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(gameResults) { gameResult ->
            GameResultCard(gameResult = gameResult)
        }
    }
}

@Composable
private fun GameResultCard(gameResult: GameResult) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Match header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = gameResult.venue,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Text(
                    text = formatGameDate(gameResult.gameDate),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Score display
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Team 1
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = gameResult.team1Name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        modifier = Modifier.size(60.dp),
                        shape = CircleShape,
                        color = if (gameResult.team1Score > gameResult.team2Score) {
                            MaterialTheme.colorScheme.primary
                        } else if (gameResult.team1Score == gameResult.team2Score) {
                            MaterialTheme.colorScheme.secondary
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = gameResult.team1Score.toString(),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (gameResult.team1Score >= gameResult.team2Score) {
                                    Color.White
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }
                    }
                }

                // VS
                Text(
                    text = "VS",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                // Team 2
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = gameResult.team2Name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        modifier = Modifier.size(60.dp),
                        shape = CircleShape,
                        color = if (gameResult.team2Score > gameResult.team1Score) {
                            MaterialTheme.colorScheme.primary
                        } else if (gameResult.team2Score == gameResult.team1Score) {
                            MaterialTheme.colorScheme.secondary
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = gameResult.team2Score.toString(),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (gameResult.team2Score >= gameResult.team1Score) {
                                    Color.White
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }
                    }
                }
            }

            // Notable player if available
            if (gameResult.notablePlayerName.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Notable Player",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Player of the Match",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        Text(
                            text = gameResult.notablePlayerName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        if (gameResult.notablePlayerReason.isNotEmpty()) {
                            Text(
                                text = gameResult.notablePlayerReason,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TeamStandingsTab(teamStats: List<TeamSeasonStats>) {
    if (teamStats.isEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = "No Standings",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No Standings Available",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Team standings will be calculated after matches are completed.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        LazyColumn {
            item {
                // Standings header
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Pos", style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(0.5f))
                        Text("Team", style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(2f))
                        Text("GP", style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(0.5f))
                        Text("W", style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(0.5f))
                        Text("D", style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(0.5f))
                        Text("L", style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(0.5f))
                        Text("GF", style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(0.5f))
                        Text("GA", style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(0.5f))
                        Text("GD", style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(0.5f))
                        Text("Pts", style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(0.5f))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(teamStats.sortedBy { it.position }) { teamStat ->
                StandingsRow(teamStat = teamStat)
            }
        }
    }
}

@Composable
private fun StandingsRow(teamStat: TeamSeasonStats) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Position with highlight for top 3
            Surface(
                modifier = Modifier.weight(0.5f),
                shape = CircleShape,
                color = when (teamStat.position) {
                    1 -> Color(0xFFFFD700) // Gold
                    2 -> Color(0xFFC0C0C0) // Silver
                    3 -> Color(0xFFCD7F32) // Bronze
                    else -> Color.Transparent
                }
            ) {
                Text(
                    text = teamStat.position.toString(),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (teamStat.position <= 3) Color.Black else MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(4.dp)
                )
            }

            Text(
                text = teamStat.teamName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(2f)
            )
            Text(teamStat.gamesPlayed.toString(), modifier = Modifier.weight(0.5f), textAlign = TextAlign.Center)
            Text(teamStat.wins.toString(), modifier = Modifier.weight(0.5f), textAlign = TextAlign.Center)
            Text(teamStat.draws.toString(), modifier = Modifier.weight(0.5f), textAlign = TextAlign.Center)
            Text(teamStat.losses.toString(), modifier = Modifier.weight(0.5f), textAlign = TextAlign.Center)
            Text(teamStat.goalsFor.toString(), modifier = Modifier.weight(0.5f), textAlign = TextAlign.Center)
            Text(teamStat.goalsAgainst.toString(), modifier = Modifier.weight(0.5f), textAlign = TextAlign.Center)
            Text(
                text = if (teamStat.goalDifference >= 0) "+${teamStat.goalDifference}" else teamStat.goalDifference.toString(),
                modifier = Modifier.weight(0.5f),
                textAlign = TextAlign.Center,
                color = when {
                    teamStat.goalDifference > 0 -> MaterialTheme.colorScheme.primary
                    teamStat.goalDifference < 0 -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.onSurface
                }
            )
            Text(
                text = teamStat.totalPoints.toString(),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(0.5f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun StatisticsTab(gameResults: List<GameResult>) {
    // Calculate aggregate statistics from game results
    val totalGoals = gameResults.sumOf { it.team1Score + it.team2Score }
    val totalMatches = gameResults.size
    val averageGoalsPerMatch = if (totalMatches > 0) totalGoals.toFloat() / totalMatches else 0f

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Tournament Statistics",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatItem(
                            label = "Total Matches",
                            value = totalMatches.toString()
                        )
                        StatItem(
                            label = "Total Goals",
                            value = totalGoals.toString()
                        )
                        StatItem(
                            label = "Avg Goals/Match",
                            value = String.format("%.1f", averageGoalsPerMatch)
                        )
                    }
                }
            }
        }

        // Add more detailed statistics as needed
    }
}

private fun formatGameDate(date: java.util.Date): String {
    val formatter = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
    return formatter.format(date)
}