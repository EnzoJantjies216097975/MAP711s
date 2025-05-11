package com.map711s.namibiahockey.ui.components.errorstates

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import com.map711s.namibiahockey.ui.components.buttons.NHUTextButton
import com.map711s.namibiahockey.ui.components.cards.NHUCard
import com.map711s.namibiahockey.ui.theme.NHUSpacing

@Composable
fun NHUErrorItem(
    errorType: ErrorType,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val icon: ImageVector = when (errorType) {
        is ErrorType.Network -> Icons.Default.CloudOff
        is ErrorType.Empty -> Icons.Default.SearchOff
        is ErrorType.NotFound -> Icons.Default.ErrorOutline
        is ErrorType.Generic -> Icons.Default.ErrorOutline
        is ErrorType.Custom -> errorType.icon ?: Icons.Default.SentimentDissatisfied
    }

    val message = when (errorType) {
        is ErrorType.Network -> "No internet connection. Please try again."
        is ErrorType.Empty -> "No results found."
        is ErrorType.NotFound -> "Item not found."
        is ErrorType.Generic -> "Something went wrong. Please try again."
        is ErrorType.Custom -> errorType.message
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(NHUSpacing.md),
        contentAlignment = Alignment.Center
    ) {
        NHUCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(NHUSpacing.md),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "Error",
                    tint = MaterialTheme.colorScheme.error
                )

                Spacer(modifier = Modifier.height(NHUSpacing.sm))

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(NHUSpacing.md))

                NHUTextButton(
                    text = "Retry",
                    onClick = onRetry
                )
            }
        }
    }
}