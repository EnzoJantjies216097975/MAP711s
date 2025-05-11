package com.map711s.namibiahockey.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.map711s.namibiahockey.R
import com.map711s.namibiahockey.theme.NHUElementSize
import com.map711s.namibiahockey.theme.NHUSpacing

sealed class ErrorType {
    object Network : ErrorType()
    object Empty : ErrorType()
    object NotFound : ErrorType()
    object Generic : ErrorType()
    data class Custom(
        val icon: ImageVector? = null,
        val title: String,
        val message: String,
        val buttonText: String = "Retry"
    ) : ErrorType()
}

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