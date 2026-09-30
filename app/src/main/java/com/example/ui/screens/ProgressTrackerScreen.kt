package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.UnitConverter
import com.example.data.model.WorkoutSessionEntity
import com.example.ui.components.WeightTrendChart
import com.example.ui.components.WorkoutsCompletedChart
import com.example.ui.theme.CalorieOrange
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DurationPurple
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.StreakGold
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.viewmodel.FitPulseViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProgressTrackerScreen(
    viewModel: FitPulseViewModel,
    modifier: Modifier = Modifier
) {
    val weightEntries by viewModel.weightEntries.collectAsStateWithLifecycle()
    val workoutSessions by viewModel.workoutSessions.collectAsStateWithLifecycle()
    val profile by viewModel.userProfile.collectAsStateWithLifecycle()
    val unit = profile.unitPrefEnum

    var showAddWeightDialog by remember { mutableStateOf(false) }

    val totalWorkouts = workoutSessions.size
    val totalVolumeKg = workoutSessions.sumOf { it.totalVolumeKg }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("progress_tracker_screen"),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "PROGRESS & ANALYTICS",
                style = MaterialTheme.typography.labelSmall,
                letterSpacing = 1.2.sp,
                color = TextSecondary
            )
            Text(
                text = "Performance Trends",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        // Streak & Overall Metrics Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Current Streak Card
                ProgressMetricStatCard(
                    title = "Streak",
                    value = "14 Days",
                    subtitle = "Personal best: 21d",
                    accentColor = StreakGold,
                    icon = Icons.Default.LocalFireDepartment,
                    modifier = Modifier.weight(1f)
                )

                // Total Workouts Card
                ProgressMetricStatCard(
                    title = "Completed",
                    value = "$totalWorkouts",
                    subtitle = "Total sessions",
                    accentColor = NeonGreen,
                    icon = Icons.Default.FitnessCenter,
                    modifier = Modifier.weight(1f)
                )

                // Total Volume Lifted Card
                ProgressMetricStatCard(
                    title = "Volume",
                    value = if (unit == com.example.data.model.UnitPreference.METRIC) {
                        String.format(Locale.US, "%.0f kg", totalVolumeKg)
                    } else {
                        String.format(Locale.US, "%.0f lbs", UnitConverter.kgToLbs(totalVolumeKg))
                    },
                    subtitle = "Weight lifted",
                    accentColor = ElectricCyan,
                    icon = Icons.Default.ShowChart,
                    modifier = Modifier.weight(1.2f)
                )
            }
        }

        // Weekly Weight Trend Section
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("weekly_weight_trend_card"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.MonitorWeight,
                                contentDescription = null,
                                tint = ElectricCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "WEIGHT TREND",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                color = TextSecondary
                            )
                        }

                        Button(
                            onClick = { showAddWeightDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan.copy(alpha = 0.15f)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("log_weight_btn")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Log Weight", color = ElectricCyan, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    WeightTrendChart(
                        entries = weightEntries,
                        unitPreference = unit
                    )
                }
            }
        }

        // Workouts Completed Frequency Bar Chart
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "WEEKLY WORKOUT ACTIVITY",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = TextSecondary
                        )

                        Text(
                            text = "This Week",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeonGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    WorkoutsCompletedChart(sessions = workoutSessions)
                }
            }
        }

        // Completed Workouts History List
        item {
            Text(
                text = "RECENT WORKOUT SESSIONS",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = TextSecondary
            )
        }

        if (workoutSessions.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No completed workouts yet. Log your first session!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }
        } else {
            items(workoutSessions, key = { it.id }) { session ->
                WorkoutHistoryCard(
                    session = session,
                    unit = unit,
                    onDelete = { viewModel.deleteWorkoutSession(session.id) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Add Weight Dialog
    if (showAddWeightDialog) {
        AddWeightEntryDialog(
            currentWeightKg = profile.weightKg,
            unit = unit,
            onDismiss = { showAddWeightDialog = false },
            onConfirm = { weightKg, note ->
                viewModel.addWeightEntry(weightKg, note)
                showAddWeightDialog = false
            }
        )
    }
}

@Composable
fun ProgressMetricStatCard(
    title: String,
    value: String,
    subtitle: String,
    accentColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = accentColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextTertiary
            )
        }
    }
}

@Composable
fun WorkoutHistoryCard(
    session: WorkoutSessionEntity,
    unit: com.example.data.model.UnitPreference,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sdf = SimpleDateFormat("MMM d, h:mm a", Locale.US)
    val dateString = sdf.format(Date(session.timestamp))
    val durationMins = session.durationSeconds / 60

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("history_card_${session.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = session.workoutTitle,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "$dateString • ${session.focusArea}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        text = "${durationMins}m duration",
                        style = MaterialTheme.typography.labelSmall,
                        color = DurationPurple
                    )
                    Text(
                        text = "${session.caloriesBurned} kcal",
                        style = MaterialTheme.typography.labelSmall,
                        color = CalorieOrange
                    )
                    Text(
                        text = "${session.completedSets} sets done",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonGreen
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete session",
                    tint = TextTertiary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun AddWeightEntryDialog(
    currentWeightKg: Double,
    unit: com.example.data.model.UnitPreference,
    onDismiss: () -> Unit,
    onConfirm: (Double, String) -> Unit
) {
    val defaultText = if (unit == com.example.data.model.UnitPreference.METRIC) {
        String.format(Locale.US, "%.1f", currentWeightKg)
    } else {
        String.format(Locale.US, "%.1f", UnitConverter.kgToLbs(currentWeightKg))
    }

    var weightInput by remember { mutableStateOf(defaultText) }
    var noteInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Today's Weight", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = weightInput,
                    onValueChange = { weightInput = it },
                    label = { Text("Weight (${UnitConverter.weightUnitLabel(unit)})") },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = DarkBorder
                    ),
                    singleLine = true
                )
                OutlinedTextField(
                    value = noteInput,
                    onValueChange = { noteInput = it },
                    label = { Text("Notes (e.g. fasted morning)") },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = DarkBorder
                    ),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val raw = weightInput.toDoubleOrNull() ?: currentWeightKg
                    val weightKg = if (unit == com.example.data.model.UnitPreference.METRIC) {
                        raw
                    } else {
                        UnitConverter.lbsToKg(raw)
                    }
                    onConfirm(weightKg, noteInput)
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan)
            ) {
                Text("Save", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}
