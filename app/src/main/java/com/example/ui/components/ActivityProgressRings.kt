package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CalorieOrange
import com.example.ui.theme.CalorieOrangeLight
import com.example.ui.theme.DurationPurple
import com.example.ui.theme.DurationPurpleLight
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonGreenLight
import com.example.ui.theme.TextPrimary

@Composable
fun MultiActivityRings(
    stepsProgress: Float, // 0f to 1f+
    caloriesProgress: Float,
    durationProgress: Float,
    modifier: Modifier = Modifier,
    size: Dp = 190.dp,
    strokeWidth: Dp = 13.dp
) {
    val animatedSteps = remember { Animatable(0f) }
    val animatedCalories = remember { Animatable(0f) }
    val animatedDuration = remember { Animatable(0f) }

    LaunchedEffect(stepsProgress, caloriesProgress, durationProgress) {
        animatedSteps.animateTo(
            targetValue = stepsProgress.coerceAtLeast(0.01f),
            animationSpec = tween(durationMillis = 1100, easing = FastOutSlowInEasing)
        )
    }
    LaunchedEffect(caloriesProgress) {
        animatedCalories.animateTo(
            targetValue = caloriesProgress.coerceAtLeast(0.01f),
            animationSpec = tween(durationMillis = 1200, delayMillis = 150, easing = FastOutSlowInEasing)
        )
    }
    LaunchedEffect(durationProgress) {
        animatedDuration.animateTo(
            targetValue = durationProgress.coerceAtLeast(0.01f),
            animationSpec = tween(durationMillis = 1300, delayMillis = 300, easing = FastOutSlowInEasing)
        )
    }

    Box(
        modifier = modifier
            .size(size)
            .testTag("multi_activity_rings"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokePx = strokeWidth.toPx()
            val spacingPx = 5.dp.toPx()

            // Outer ring: Steps (Neon Green)
            val outerRadius = (size.toPx() / 2f) - (strokePx / 2f)
            val outerSize = Size(outerRadius * 2f, outerRadius * 2f)
            val outerTopLeft = Offset(center.x - outerRadius, center.y - outerRadius)

            // Middle ring: Calories (Orange)
            val middleRadius = outerRadius - strokePx - spacingPx
            val middleSize = Size(middleRadius * 2f, middleRadius * 2f)
            val middleTopLeft = Offset(center.x - middleRadius, center.y - middleRadius)

            // Inner ring: Duration (Purple)
            val innerRadius = middleRadius - strokePx - spacingPx
            val innerSize = Size(innerRadius * 2f, innerRadius * 2f)
            val innerTopLeft = Offset(center.x - innerRadius, center.y - innerRadius)

            // Background tracks
            drawArc(
                color = NeonGreen.copy(alpha = 0.15f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = outerTopLeft,
                size = outerSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )
            drawArc(
                color = CalorieOrange.copy(alpha = 0.15f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = middleTopLeft,
                size = middleSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )
            drawArc(
                color = DurationPurple.copy(alpha = 0.15f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = innerTopLeft,
                size = innerSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )

            // Active Sweeps
            val stepsAngle = (animatedSteps.value.coerceIn(0f, 1f)) * 360f
            if (stepsAngle > 0f) {
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(NeonGreen, NeonGreenLight, NeonGreen),
                        center = center
                    ),
                    startAngle = -90f,
                    sweepAngle = stepsAngle,
                    useCenter = false,
                    topLeft = outerTopLeft,
                    size = outerSize,
                    style = Stroke(width = strokePx, cap = StrokeCap.Round)
                )
            }

            val caloriesAngle = (animatedCalories.value.coerceIn(0f, 1f)) * 360f
            if (caloriesAngle > 0f) {
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(CalorieOrange, CalorieOrangeLight, CalorieOrange),
                        center = center
                    ),
                    startAngle = -90f,
                    sweepAngle = caloriesAngle,
                    useCenter = false,
                    topLeft = middleTopLeft,
                    size = middleSize,
                    style = Stroke(width = strokePx, cap = StrokeCap.Round)
                )
            }

            val durationAngle = (animatedDuration.value.coerceIn(0f, 1f)) * 360f
            if (durationAngle > 0f) {
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(DurationPurple, DurationPurpleLight, DurationPurple),
                        center = center
                    ),
                    startAngle = -90f,
                    sweepAngle = durationAngle,
                    useCenter = false,
                    topLeft = innerTopLeft,
                    size = innerSize,
                    style = Stroke(width = strokePx, cap = StrokeCap.Round)
                )
            }
        }

        // Center visual icon / status
        Box(
            modifier = Modifier.padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.DirectionsRun,
                contentDescription = "Fitness activity",
                tint = NeonGreen,
                modifier = Modifier.size(34.dp)
            )
        }
    }
}

@Composable
fun SingleCircularProgress(
    progress: Float,
    color: Color,
    gradientColor: Color,
    modifier: Modifier = Modifier,
    size: Dp = 68.dp,
    strokeWidth: Dp = 6.dp,
    content: @Composable () -> Unit
) {
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(progress) {
        animatedProgress.animateTo(
            targetValue = progress.coerceIn(0f, 1f),
            animationSpec = tween(900, easing = FastOutSlowInEasing)
        )
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokePx = strokeWidth.toPx()
            val arcRadius = (size.toPx() / 2f) - (strokePx / 2f)
            val arcSize = Size(arcRadius * 2f, arcRadius * 2f)
            val topLeft = Offset(center.x - arcRadius, center.y - arcRadius)

            // Background
            drawArc(
                color = color.copy(alpha = 0.15f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )

            // Progress
            val sweep = animatedProgress.value * 360f
            if (sweep > 0f) {
                drawArc(
                    brush = Brush.linearGradient(listOf(color, gradientColor)),
                    startAngle = -90f,
                    sweepAngle = sweep,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokePx, cap = StrokeCap.Round)
                )
            }
        }
        content()
    }
}
