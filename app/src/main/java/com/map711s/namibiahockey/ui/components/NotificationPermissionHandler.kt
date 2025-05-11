package com.map711s.namibiahockey.ui.components

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.map711s.namibiahockey.theme.NHUSpacing
import com.map711s.namibiahockey.util.NotificationManager

@Composable
fun NotificationPermissionHandler(
    notificationManager: NotificationManager,
    onPermissionResult: (Boolean) -> Unit = {}
) {
    val context = LocalContext.current
    var showPermissionDialog by remember { mutableStateOf(false) }

    // Create permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        onPermissionResult(isGranted)
    }

    // Check if we need to request permission
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!notificationManager.hasNotificationPermission()) {
                showPermissionDialog = true
            }
        }
    }

    // Show permission rationale dialog
    if (showPermissionDialog) {
        NHUDialog(
            title = "Stay Updated",
            message = "Enable notifications to receive updates about events, news, and team activities.",
            confirmButtonText = "Enable",
            dismissButtonText = "Not Now",
            onDismiss = { showPermissionDialog = false },
            onConfirm = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                showPermissionDialog = false
            }
        )
    }
}

@Composable
fun NHUDialog(
    title: String,
    message: String,
    confirmButtonText: String,
    dismissButtonText: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(NHUSpacing.lg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(NHUSpacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(NHUSpacing.md))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(NHUSpacing.lg))

            NHUPrimaryButton(
                text = confirmButtonText,
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(NHUSpacing.sm))

            NHUTextButton(
                text = dismissButtonText,
                onClick = onDismiss
            )
        }
    }
}