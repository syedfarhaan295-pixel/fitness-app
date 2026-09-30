package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.WorkoutSessionEntity
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonGreenLight
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextTertiary

data class DayWorkoutCount(
    val dayName: String,
    val count: Int,
    val isToday: Boolean = false
)

@Composable
fun WorkoutsCompletedChart(
    sessions: List<WorkoutSessionEntity>,
    modifier: Modifier = Modifier
) {
    // Generate 7 days of the week representation
    val days = listOf(
        DayWorkoutCount("Mon", 1),
        DayWorkoutCount("Tue", 0),
        DayWorkoutCount("Wed", 2),
        DayWorkoutCount("Thu", 1),
        DayWorkoutCount("Fri", 0),
        DayWorkoutCount("Sat", 1),
        DayWorkoutCount("Sun", 1, isToday = true)
    )

    val maxCount = maxOf(2, days.maxOfOrNull { it.count } ?: 2)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("workouts_completed_chart")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            days.forEach { day ->
                val heightFraction = if (maxCount > 0) (day.count.toFloat() / maxCount.toFloat()).coerceIn(0.08f, 1f) else 0.08f

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    if (day.count > 0) {
                        Text(
                            text = "${day.count}",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (day.isToday) NeonGreen else TextPrimary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    } else {
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    Box(
                        modifier = Modifier
                            .width(18.dp)
                            .height(80.dp)
                            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        // Background slot
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .background(DarkBorder.copy(alpha = 0.5f))
                        )

                        // Active bar
                        if (day.count > 0) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(heightFraction)
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(NeonGreenLight, NeonGreen)
                                        )
                                    )
                            )
                        }
                    }

                    Text(
                        text = day.dayName,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (day.isToday) NeonGreen else TextTertiary,
                        fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}
