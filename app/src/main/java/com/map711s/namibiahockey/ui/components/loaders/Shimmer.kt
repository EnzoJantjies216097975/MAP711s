package com.map711s.namibiahockey.ui.components.loaders

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.map711s.namibiahockey.ui.theme.NHUSpacing

fun Modifier.shimmerEffect(): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                delayMillis = 300
            )
        ),
        label = "shimmerAnimation"
    )

    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.5f),
        Color.LightGray.copy(alpha = 0.2f)
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim, translateAnim),
        end = Offset(translateAnim + 100f, translateAnim + 100f)
    )

    background(brush)
}

@Composable
fun ShimmerListItem(
    modifier: Modifier = Modifier,
    isLoading: Boolean = true,
    contentAfterLoading: @Composable () -> Unit = {}
) {
    if (isLoading) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(NHUSpacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .shimmerEffect()
            )

            Spacer(modifier = Modifier.width(NHUSpacing.md))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(20.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .shimmerEffect()
                )

                Spacer(modifier = Modifier.height(NHUSpacing.sm))

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .shimmerEffect()
                )

                Spacer(modifier = Modifier.height(NHUSpacing.sm))

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .shimmerEffect()
                )
            }
        }
    } else {
        contentAfterLoading()
    }
}

@Composable
fun ShimmerCardItem(
    modifier: Modifier = Modifier,
    isLoading: Boolean = true,
    contentAfterLoading: @Composable () -> Unit = {}
) {
    if (isLoading) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(NHUSpacing.md)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .shimmerEffect()
            )

            Spacer(modifier = Modifier.height(NHUSpacing.md))

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(20.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmerEffect()
            )

            Spacer(modifier = Modifier.height(NHUSpacing.sm))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmerEffect()
            )

            Spacer(modifier = Modifier.height(NHUSpacing.sm))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .shimmerEffect()
                )

                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .shimmerEffect()
                )
            }
        }
    } else {
        contentAfterLoading()
    }
}

@Composable
fun EventListShimmer(
    itemCount: Int = 3
) {
    Column {
        repeat(itemCount) {
            ShimmerCardItem()
            Spacer(modifier = Modifier.height(NHUSpacing.md))
        }
    }
}

@Composable
fun NewsListShimmer(
    itemCount: Int = 3
) {
    Column {
        repeat(itemCount) {
            ShimmerCardItem()
            Spacer(modifier = Modifier.height(NHUSpacing.md))
        }
    }
}

@Composable
fun PlayerListShimmer(
    itemCount: Int = 5
) {
    Column {
        repeat(itemCount) {
            ShimmerListItem()
            Spacer(modifier = Modifier.height(NHUSpacing.sm))
        }
    }
}