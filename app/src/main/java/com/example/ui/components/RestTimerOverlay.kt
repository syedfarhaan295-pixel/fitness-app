package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.ElectricCyanLight
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun InteractiveRestTimerCard(
    secondsRemaining: Int,
    totalSeconds: Int,
    isRunning: Boolean,
    onStartTimer: (Int) -> Unit,
    onPauseTimer: () -> Unit,
    onResumeTimer: () -> Unit,
    onAddSeconds: (Int) -> Unit,
    onResetTimer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val presets = listOf(30, 60, 90, 120)
    val progress = if (totalSeconds > 0) (secondsRemaining.toFloat() / totalSeconds.toFloat()).coerceIn(0f, 1f) else 0f
    val minutes = secondsRemaining / 60
    val secs = secondsRemaining % 60
    val timeFormatted = String.format(Locale.US, "%02d:%02d", minutes, secs)

    val isFinished = secondsRemaining == 0 && totalSeconds > 0 && !isRunning

    val pulseTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by pulseTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("rest_timer_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = DarkSurfaceElevated
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isRunning) ElectricCyan.copy(alpha = 0.6f) else DarkBorder
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = "Rest timer",
                        tint = if (isRunning) ElectricCyan else TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "REST TIMER",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = if (isRunning) ElectricCyan else TextSecondary
                    )
                }

                if (secondsRemaining > 0) {
                    IconButton(
                        onClick = onResetTimer,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("timer_reset_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Stop rest timer",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Timer display & controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Countdown text with circular visual
                SingleCircularProgress(
                    progress = progress,
                    color = ElectricCyan,
                    gradientColor = NeonGreen,
                    size = 72.dp,
                    strokeWidth = 6.dp
                ) {
                    Text(
                        text = timeFormatted,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isRunning) TextPrimary else TextSecondary
                    )
                }

                // Action buttons: Pause/Play, +15s
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // +15s chip button
                    Surface(
                        onClick = { onAddSeconds(15) },
                        shape = RoundedCornerShape(12.dp),
                        color = ElectricCyan.copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.4f)),
                        modifier = Modifier.testTag("timer_add_15s_btn")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "+15s",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = ElectricCyan
                            )
                        }
                    }

                    // Play / Pause button
                    Surface(
                        onClick = {
                            if (isRunning) onPauseTimer()
                            else if (secondsRemaining > 0) onResumeTimer()
                            else onStartTimer(60)
                        },
                        shape = CircleShape,
                        color = if (isRunning) NeonGreen else ElectricCyan,
                        modifier = Modifier
                            .size(46.dp)
                            .testTag("timer_toggle_btn")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isRunning) "Pause timer" else "Start timer",
                                tint = Color.Black,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Presets row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                presets.forEach { sec ->
                    val isSelected = totalSeconds == sec && secondsRemaining > 0
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isSelected) ElectricCyan.copy(alpha = 0.25f)
                                else DarkBorder.copy(alpha = 0.5f)
                            )
                            .border(
                                1.dp,
                                if (isSelected) ElectricCyan else Color.Transparent,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { onStartTimer(sec) }
                            .padding(vertical = 8.dp)
                            .testTag("preset_${sec}s"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${sec}s",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) ElectricCyanLight else TextSecondary
                        )
                    }
                }
            }
        }
    }
}
