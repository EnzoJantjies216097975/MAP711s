package com.map711s.namibiahockey.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.map711s.namibiahockey.data.model.HockeyType

@Composable
fun HockeyTypeOptions(
    selectedType: HockeyType,
    onTypeSelected: (HockeyType) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(8.dp)
    ) {
        HockeyType.entries.forEach { type ->
            if (type != HockeyType.BOTH) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (type == selectedType),
                            onClick = { onTypeSelected(type) },
                            role = Role.RadioButton
                        )
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (type == selectedType),
                        onClick = null
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = "${type.name.lowercase().replaceFirstChar { it.uppercase() }} Hockey",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        val description = when (type) {
                            HockeyType.OUTDOOR -> "Field hockey played on grass or turf fields"
                            HockeyType.INDOOR -> "Hockey played in indoor courts with different rules"
                            else -> ""
                        }

                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }

                if (type != HockeyType.entries.last { it != HockeyType.BOTH }) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                    )
                }
            }
        }
    }
}

@Composable
fun HockeyTypeHeader(hockeyType: HockeyType) {
    val backgroundColor = when (hockeyType) {
        HockeyType.OUTDOOR -> MaterialTheme.colorScheme.primaryContainer
        HockeyType.INDOOR -> MaterialTheme.colorScheme.secondaryContainer
        else -> MaterialTheme.colorScheme.tertiaryContainer
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (hockeyType == HockeyType.OUTDOOR)
                    Icons.Default.Landscape
                else
                    Icons.Default.Home,
                contentDescription = null,
                tint = when (hockeyType) {
                    HockeyType.OUTDOOR -> MaterialTheme.colorScheme.onPrimaryContainer
                    HockeyType.INDOOR -> MaterialTheme.colorScheme.onSecondaryContainer
                    else -> MaterialTheme.colorScheme.onTertiaryContainer
                }
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "${hockeyType.name.lowercase().replaceFirstChar { it.uppercase() }} Hockey Events",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = when (hockeyType) {
                    HockeyType.OUTDOOR -> MaterialTheme.colorScheme.onPrimaryContainer
                    HockeyType.INDOOR -> MaterialTheme.colorScheme.onSecondaryContainer
                    else -> MaterialTheme.colorScheme.onTertiaryContainer
                }
            )
        }
    }
}