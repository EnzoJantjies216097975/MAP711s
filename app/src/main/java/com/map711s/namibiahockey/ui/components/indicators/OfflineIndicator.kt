package com.map711s.namibiahockey.ui.components.indicators

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.map711s.namibiahockey.data.local.OfflineOperationQueue
import com.map711s.namibiahockey.ui.theme.NHUSpacing
import com.map711s.namibiahockey.util.NetworkMonitor
import kotlinx.coroutines.delay

@Composable
fun OfflineIndicator(
    networkMonitor: NetworkMonitor,
    offlineQueue: OfflineOperationQueue,
    modifier: Modifier = Modifier
) {
    val isOnline by networkMonitor.isOnline.collectAsState(initial = true)
    var hasPendingOperations by remember { mutableStateOf(false) }
    var isSyncing by remember { mutableStateOf(false) }

    // Check for pending operations
    LaunchedEffect(isOnline) {
        if (isOnline) {
            val operations = offlineQueue.getOperations()
            hasPendingOperations = operations.isNotEmpty()

            if (hasPendingOperations) {
                isSyncing = true
                delay(2000) // Show syncing for at least 2 seconds
                offlineQueue.processQueuedOperations()
                delay(1000) // Additional delay after processing
                isSyncing = false
                hasPendingOperations = false
            }
        }
    }

    AnimatedVisibility(
        visible = !isOnline || isSyncing,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(
                    if (isSyncing) MaterialTheme.colorScheme.tertiaryContainer
                    else MaterialTheme.colorScheme.errorContainer
                )
                .padding(NHUSpacing.md),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isSyncing) Icons.Default.Sync else Icons.Default.CloudOff,
                    contentDescription = if (isSyncing) "Syncing" else "Offline",
                    tint = if (isSyncing) MaterialTheme.colorScheme.onTertiaryContainer
                    else MaterialTheme.colorScheme.onErrorContainer
                )

                Spacer(modifier = Modifier.width(NHUSpacing.md))

                Text(
                    text = if (isSyncing) "Syncing changes..."
                    else "You're offline. Changes will sync when connection is restored.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isSyncing) MaterialTheme.colorScheme.onTertiaryContainer
                    else MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}