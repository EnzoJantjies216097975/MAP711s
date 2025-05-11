package com.map711s.namibiahockey.util

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.drawscope.draw
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize

@Composable
fun DrawingCache(
    invalidationKey: Any?,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    var size by remember { mutableStateOf(IntSize.Zero) }

    // This Box will cache its drawing operations until invalidationKey changes
    Box(
        modifier = modifier
            .onSizeChanged { size = it }
            .drawWithCache {
                // Cache is rebuilt when invalidationKey changes
                onDrawWithContent {
                    // Only draw if we have a size
                    if (size != IntSize.Zero) {
                        draw { content() }
                    }
                }
            }
    ) {
        // Content is only composed once per invalidationKey change
        if (size != IntSize.Zero) {
            content()
        }
    }
}