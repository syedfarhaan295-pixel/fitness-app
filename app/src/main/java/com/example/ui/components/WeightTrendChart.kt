package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UnitConverter
import com.example.data.model.UnitPreference
import com.example.data.model.WeightEntryEntity
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.ElectricCyanLight
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import java.util.Locale

@Composable
fun WeightTrendChart(
    entries: List<WeightEntryEntity>,
    unitPreference: UnitPreference,
    modifier: Modifier = Modifier
) {
    if (entries.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(180.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No weight records yet. Log your weight to see trends!",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
        return
    }

    var selectedIndex by remember { mutableStateOf<Int?>(entries.lastIndex) }

    val weights = entries.map {
        if (unitPreference == UnitPreference.METRIC) it.weightKg else UnitConverter.kgToLbs(it.weightKg)
    }
    val minWeight = weights.minOrNull() ?: 0.0
    val maxWeight = weights.maxOrNull() ?: 100.0
    val paddingWeight = ((maxWeight - minWeight).coerceAtLeast(1.0)) * 0.25
    val chartMin = minWeight - paddingWeight
    val chartMax = maxWeight + paddingWeight

    val unitLabel = UnitConverter.weightUnitLabel(unitPreference)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("weight_trend_chart")
    ) {
        // Top stats & selected tooltip summary
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val currentIdx = selectedIndex ?: entries.lastIndex
            val currentEntry = entries.getOrNull(currentIdx)
            val currentVal = weights.getOrNull(currentIdx) ?: 0.0

            Column {
                Text(
                    text = currentEntry?.dateLabel ?: "Latest",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary
                )
                Text(
                    text = String.format(Locale.US, "%.1f %s", currentVal, unitLabel),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = ElectricCyan
                )
            }

            if (weights.size >= 2) {
                val diff = weights.last() - weights.first()
                val isLoss = diff <= 0
                Box(
                    modifier = Modifier
                        .background(
                            if (isLoss) NeonGreen.copy(alpha = 0.15f) else Color(0xFFFF5252).copy(alpha = 0.15f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = String.format(Locale.US, "%s%.1f %s", if (diff > 0) "+" else "", diff, unitLabel),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isLoss) NeonGreen else Color(0xFFFF5252)
                    )
                }
            }
        }

        // Canvas line chart
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(entries) {
                        detectTapGestures { offset ->
                            val width = size.width
                            val stepX = if (entries.size > 1) width / (entries.size - 1) else width
                            val tappedIndex = (offset.x / stepX).toInt().coerceIn(0, entries.lastIndex)
                            selectedIndex = tappedIndex
                        }
                    }
            ) {
                val w = size.width
                val h = size.height
                val count = entries.size

                // Horizontal grid lines
                val gridLines = 3
                for (i in 0..gridLines) {
                    val y = h * (i.toFloat() / gridLines)
                    drawLine(
                        color = DarkBorder.copy(alpha = 0.6f),
                        start = Offset(0f, y),
                        end = Offset(w, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                if (count < 2) {
                    // Single point
                    val singleY = h / 2f
                    drawCircle(
                        color = ElectricCyan,
                        radius = 6.dp.toPx(),
                        center = Offset(w / 2f, singleY)
                    )
                    return@Canvas
                }

                val stepX = w / (count - 1).toFloat()
                val points = weights.mapIndexed { index, weight ->
                    val x = index * stepX
                    val yFraction = ((weight - chartMin) / (chartMax - chartMin)).toFloat().coerceIn(0f, 1f)
                    val y = h - (yFraction * h)
                    Offset(x, y)
                }

                // Fill gradient path
                val fillPath = Path().apply {
                    moveTo(points.first().x, h)
                    lineTo(points.first().x, points.first().y)
                    for (i in 1 until points.size) {
                        val p0 = points[i - 1]
                        val p1 = points[i]
                        val cx = (p0.x + p1.x) / 2f
                        cubicTo(cx, p0.y, cx, p1.y, p1.x, p1.y)
                    }
                    lineTo(points.last().x, h)
                    close()
                }

                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(ElectricCyan.copy(alpha = 0.35f), Color.Transparent),
                        startY = 0f,
                        endY = h
                    )
                )

                // Line path
                val strokePath = Path().apply {
                    moveTo(points.first().x, points.first().y)
                    for (i in 1 until points.size) {
                        val p0 = points[i - 1]
                        val p1 = points[i]
                        val cx = (p0.x + p1.x) / 2f
                        cubicTo(cx, p0.y, cx, p1.y, p1.x, p1.y)
                    }
                }

                drawPath(
                    path = strokePath,
                    brush = Brush.horizontalGradient(
                        listOf(ElectricCyan, ElectricCyanLight)
                    ),
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )

                // Draw circles for points
                points.forEachIndexed { idx, point ->
                    val isSelected = (idx == selectedIndex)
                    if (isSelected) {
                        // Halo
                        drawCircle(
                            color = ElectricCyan.copy(alpha = 0.3f),
                            radius = 11.dp.toPx(),
                            center = point
                        )
                    }
                    drawCircle(
                        color = if (isSelected) Color.White else ElectricCyan,
                        radius = if (isSelected) 5.dp.toPx() else 3.5.dp.toPx(),
                        center = point
                    )
                }
            }
        }

        // X-Axis labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            entries.forEachIndexed { index, entry ->
                Text(
                    text = entry.dateLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (index == selectedIndex) ElectricCyan else TextTertiary,
                    fontWeight = if (index == selectedIndex) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}
