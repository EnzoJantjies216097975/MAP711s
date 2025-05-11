package com.map711s.namibiahockey.ui.components.buttons

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.map711s.namibiahockey.ui.theme.NHUElementSize
import com.map711s.namibiahockey.ui.theme.NHUSpacing

@Composable
fun NHUSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    icon: ImageVector? = null,
    contentDescription: String? = null
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(NHUElementSize.buttonHeight),
        enabled = enabled && !isLoading,
        contentPadding = PaddingValues(horizontal = NHUSpacing.md)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.height(24.dp),
                strokeWidth = 2.dp
            )
        } else {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = contentDescription,
                    modifier = Modifier.height(24.dp)
                )
            }
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}