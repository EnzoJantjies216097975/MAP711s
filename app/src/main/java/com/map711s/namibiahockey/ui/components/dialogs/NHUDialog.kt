package com.map711s.namibiahockey.ui.components.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.map711s.namibiahockey.ui.components.buttons.NHUPrimaryButton
import com.map711s.namibiahockey.ui.components.buttons.NHUTextButton
import com.map711s.namibiahockey.ui.theme.NHUSpacing

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