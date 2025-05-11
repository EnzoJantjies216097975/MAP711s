package com.map711s.namibiahockey.ui.components.navigation

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun NHUFloatingActionButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String?,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary
) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = containerColor,
        contentColor = contentColor,
        shape = CircleShape
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription
        )
    }
}