package com.map711s.namibiahockey.util

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.toComposeRect
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.window.layout.WindowMetricsCalculator

enum class WindowSizeClass { COMPACT, MEDIUM, EXPANDED }

data class WindowSize(
    val width: WindowSizeClass,
    val height: WindowSizeClass
)

/**
 * Calculate and remember window size class based on window metrics
 */
@Composable
fun Activity.rememberWindowSize(): WindowSize {
    val configuration = LocalConfiguration.current
    val windowMetrics = remember(configuration) {
        WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(this)
    }

    val windowDpSize = with(LocalDensity.current) {
        windowMetrics.bounds.toComposeRect().size.toDpSize()
    }

    val widthWindowSizeClass = when {
        windowDpSize.width < 600.dp -> WindowSizeClass.COMPACT
        windowDpSize.width < 840.dp -> WindowSizeClass.MEDIUM
        else -> WindowSizeClass.EXPANDED
    }

    val heightWindowSizeClass = when {
        windowDpSize.height < 480.dp -> WindowSizeClass.COMPACT
        windowDpSize.height < 900.dp -> WindowSizeClass.MEDIUM
        else -> WindowSizeClass.EXPANDED
    }

    return WindowSize(widthWindowSizeClass, heightWindowSizeClass)
}

/**
 * Calculate content padding based on window size
 */
@Composable
fun rememberContentPadding(windowSize: WindowSize): Dp {
    return when (windowSize.width) {
        WindowSizeClass.COMPACT -> 16.dp
        WindowSizeClass.MEDIUM -> 24.dp
        WindowSizeClass.EXPANDED -> 32.dp
    }
}

/**
 * Determine if the layout should use a two-column design
 */
@Composable
fun shouldUseTwoColumnLayout(windowSize: WindowSize): Boolean {
    return windowSize.width == WindowSizeClass.EXPANDED
}