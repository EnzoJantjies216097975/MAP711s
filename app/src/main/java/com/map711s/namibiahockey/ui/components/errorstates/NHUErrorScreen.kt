package com.map711s.namibiahockey.ui.components.errorstates

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import com.map711s.namibiahockey.R
import com.map711s.namibiahockey.ui.components.buttons.NHUPrimaryButton
import com.map711s.namibiahockey.ui.theme.NHUElementSize
import com.map711s.namibiahockey.ui.theme.NHUSpacing

@Composable
fun NHUErrorScreen(
    errorType: ErrorType,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val icon: (@Composable () -> Unit) = when (errorType) {
        is ErrorType.Network -> {
            {
                Icon(
                    imageVector = Icons.Default.CloudOff,
                    contentDescription = "Network Error",
                    modifier = Modifier.size(NHUElementSize.largeIcon * 2),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
        is ErrorType.Empty -> {
            {
                Icon(
                    imageVector = Icons.Default.SearchOff,
                    contentDescription = "No Results",
                    modifier = Modifier.size(NHUElementSize.largeIcon * 2),
                    tint = MaterialTheme.colorScheme.tertiary
                )
            }
        }
        is ErrorType.NotFound -> {
            {
                Image(
                    painter = painterResource(id = R.drawable.ic_not_found),
                    contentDescription = "Not Found",
                    modifier = Modifier.size(NHUElementSize.largeIcon * 2)
                )
            }
        }
        is ErrorType.Generic -> {
            {
                Icon(
                    imageVector = Icons.Default.ErrorOutline,
                    contentDescription = "Error",
                    modifier = Modifier.size(NHUElementSize.largeIcon * 2),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
        is ErrorType.Custom -> {
            {
                errorType.icon?.let {
                    Icon(
                        imageVector = it,
                        contentDescription = errorType.title,
                        modifier = Modifier.size(NHUElementSize.largeIcon * 2),
                        tint = MaterialTheme.colorScheme.error
                    )
                } ?: Icon(
                    imageVector = Icons.Default.SentimentDissatisfied,
                    contentDescription = errorType.title,
                    modifier = Modifier.size(NHUElementSize.largeIcon * 2),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }

    val title = when (errorType) {
        is ErrorType.Network -> "No Internet Connection"
        is ErrorType.Empty -> "No Results Found"
        is ErrorType.NotFound -> "Item Not Found"
        is ErrorType.Generic -> "Something Went Wrong"
        is ErrorType.Custom -> errorType.title
    }

    val message = when (errorType) {
        is ErrorType.Network -> "Please check your internet connection and try again."
        is ErrorType.Empty -> "We couldn't find any matches for your search."
        is ErrorType.NotFound -> "The item you're looking for doesn't exist or was removed."
        is ErrorType.Generic -> "An unexpected error occurred. Please try again later."
        is ErrorType.Custom -> errorType.message
    }

    val buttonText = when (errorType) {
        is ErrorType.Network -> "Retry"
        is ErrorType.Empty -> "Clear Filters"
        is ErrorType.NotFound -> "Go Back"
        is ErrorType.Generic -> "Try Again"
        is ErrorType.Custom -> errorType.buttonText
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(NHUSpacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            icon()

            Spacer(modifier = Modifier.height(NHUSpacing.lg))

            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(NHUSpacing.md))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(NHUSpacing.xl))

            NHUPrimaryButton(
                text = buttonText,
                onClick = onRetry,
                modifier = Modifier.fillMaxWidth(0.8f)
            )
        }
    }
}