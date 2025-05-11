package com.map711s.namibiahockey.theme

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Design system tokens for consistent spacing, sizing, and other dimensions.
 */
object NHUSpacing {
    val xxs = 2.dp
    val xs = 4.dp
    val sm = 8.dp
    val md = 16.dp
    val lg = 24.dp
    val xl = 32.dp
    val xxl = 48.dp
    val xxxl = 64.dp
}

/**
 * Standard corner radii for various component types
 */
object NHUCorners {
    val none = 0.dp
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 24.dp
    val full = 9999.dp // Effectively a circle for most components
}

/**
 * Typography scale adjustments (built on top of Material Theme Typography)
 */
object NHUFontSize {
    val xs = 12.sp
    val sm = 14.sp
    val md = 16.sp
    val lg = 18.sp
    val xl = 20.sp
    val xxl = 24.sp
    val xxxl = 32.sp
}

/**
 * Common element sizes
 */
object NHUElementSize {
    val buttonHeight = 48.dp
    val smallButtonHeight = 36.dp
    val iconButton = 48.dp
    val smallIconButton = 36.dp
    val icon = 24.dp
    val smallIcon = 16.dp
    val largeIcon = 48.dp
    val checkbox = 24.dp
    val avatar = 40.dp
    val smallAvatar = 32.dp
    val largeAvatar = 64.dp
}