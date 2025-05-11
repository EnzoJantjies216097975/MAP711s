package com.map711s.namibiahockey.ui.components.dialogs

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
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

