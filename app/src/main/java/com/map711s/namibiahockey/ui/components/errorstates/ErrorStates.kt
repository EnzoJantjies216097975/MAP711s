package com.map711s.namibiahockey.ui.components.errorstates

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
import com.map711s.namibiahockey.R
import com.map711s.namibiahockey.ui.components.buttons.NHUPrimaryButton
import com.map711s.namibiahockey.ui.components.buttons.NHUTextButton
import com.map711s.namibiahockey.ui.components.cards.NHUCard
import com.map711s.namibiahockey.ui.theme.NHUElementSize
import com.map711s.namibiahockey.ui.theme.NHUSpacing

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