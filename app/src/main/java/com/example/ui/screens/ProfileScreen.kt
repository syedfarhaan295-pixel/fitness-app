package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.UnitConverter
import com.example.data.model.UnitPreference
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
import com.example.ui.theme.WaterBlue
import com.example.viewmodel.FitPulseViewModel
import java.util.Locale

@Composable
fun ProfileScreen(
    viewModel: FitPulseViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.userProfile.collectAsStateWithLifecycle()
    val unit = profile.unitPrefEnum

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showEditGoalsDialog by remember { mutableStateOf(false) }

    // BMI calculation: weight / (height/100)^2
    val heightM = profile.heightCm / 100.0
    val bmi = if (heightM > 0) profile.weightKg / (heightM * heightM) else 23.5

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("profile_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "USER PROFILE",
                style = MaterialTheme.typography.labelSmall,
                letterSpacing = 1.2.sp,
                color = TextSecondary
            )
            Text(
                text = "Account & Settings",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        // User Avatar & Name Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_user_card"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(NeonGreen.copy(alpha = 0.15f), CircleShape)
                            .border(2.dp, NeonGreen, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Avatar",
                            tint = NeonGreen,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = profile.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Pro Athlete Member",
                            style = MaterialTheme.typography.labelSmall,
                            color = ElectricCyan
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = DarkBorder
                            ) {
                                Text(
                                    text = "BMI ${String.format(Locale.US, "%.1f", bmi)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    IconButton(
                        onClick = { showEditProfileDialog = true },
                        modifier = Modifier.testTag("edit_profile_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit profile",
                            tint = TextSecondary
                        )
                    }
                }
            }
        }

        // Unit Preference Toggle Card (Metric vs Imperial)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("unit_toggle_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "UNIT PREFERENCE",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                color = TextSecondary
                            )
                            Text(
                                text = if (unit == UnitPreference.METRIC) "Metric (kg, cm, ml)" else "Imperial (lbs, in, fl oz)",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextTertiary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Segmented Toggle Control
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkSurfaceElevated)
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val isMetric = unit == UnitPreference.METRIC

                        // Metric Option
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isMetric) NeonGreen else Color.Transparent)
                                .clickable {
                                    if (!isMetric) viewModel.toggleUnitPreference()
                                }
                                .padding(vertical = 10.dp)
                                .testTag("unit_metric_btn"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Metric (kg / cm)",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isMetric) Color.Black else TextSecondary
                            )
                        }

                        // Imperial Option
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (!isMetric) ElectricCyan else Color.Transparent)
                                .clickable {
                                    if (isMetric) viewModel.toggleUnitPreference()
                                }
                                .padding(vertical = 10.dp)
                                .testTag("unit_imperial_btn"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Imperial (lbs / in)",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (!isMetric) Color.Black else TextSecondary
                            )
                        }
                    }
                }
            }
        }

        // Personal Metrics Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PERSONAL METRICS",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = TextSecondary
                        )

                        TextButton(onClick = { showEditProfileDialog = true }) {
                            Text("Edit", color = ElectricCyan)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ProfileMetricBox(
                            label = "Age",
                            value = "${profile.age}",
                            unit = "years",
                            modifier = Modifier.weight(1f)
                        )
                        ProfileMetricBox(
                            label = "Height",
                            value = profile.displayHeight(unit),
                            unit = "",
                            modifier = Modifier.weight(1f)
                        )
                        ProfileMetricBox(
                            label = "Weight",
                            value = profile.displayWeight(unit),
                            unit = "",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Target Goals Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "DAILY TARGET GOALS",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = TextSecondary
                        )

                        TextButton(
                            onClick = { showEditGoalsDialog = true },
                            modifier = Modifier.testTag("edit_goals_btn")
                        ) {
                            Text("Edit Goals", color = NeonGreen)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    GoalItemRow(
                        icon = Icons.Default.DirectionsRun,
                        accentColor = NeonGreen,
                        title = "Daily Steps Goal",
                        value = String.format(Locale.US, "%,d steps", profile.stepGoal)
                    )
                    GoalItemRow(
                        icon = Icons.Default.LocalFireDepartment,
                        accentColor = CalorieOrange,
                        title = "Active Calorie Burn",
                        value = "${profile.calorieGoal} kcal"
                    )
                    GoalItemRow(
                        icon = Icons.Default.Timer,
                        accentColor = DurationPurple,
                        title = "Workout Duration",
                        value = "${profile.durationGoalMinutes} minutes"
                    )
                    GoalItemRow(
                        icon = Icons.Default.Opacity,
                        accentColor = WaterBlue,
                        title = "Hydration Intake",
                        value = UnitConverter.formatWater(profile.waterGoalMl, unit)
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Edit Profile Modal
    if (showEditProfileDialog) {
        EditProfileDialog(
            profile = profile,
            unit = unit,
            onDismiss = { showEditProfileDialog = false },
            onSave = { name, age, heightCm, weightKg ->
                viewModel.updateProfile(
                    name = name,
                    age = age,
                    heightCm = heightCm,
                    weightKg = weightKg,
                    stepGoal = profile.stepGoal,
                    calorieGoal = profile.calorieGoal,
                    durationGoalMinutes = profile.durationGoalMinutes,
                    waterGoalMl = profile.waterGoalMl
                )
                showEditProfileDialog = false
            }
        )
    }

    // Edit Goals Modal
    if (showEditGoalsDialog) {
        EditGoalsDialog(
            profile = profile,
            onDismiss = { showEditGoalsDialog = false },
            onSave = { steps, calories, duration, water ->
                viewModel.updateProfile(
                    name = profile.name,
                    age = profile.age,
                    heightCm = profile.heightCm,
                    weightKg = profile.weightKg,
                    stepGoal = steps,
                    calorieGoal = calories,
                    durationGoalMinutes = duration,
                    waterGoalMl = water
                )
                showEditGoalsDialog = false
            }
        )
    }
}

@Composable
fun ProfileMetricBox(
    label: String,
    value: String,
    unit: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(DarkSurfaceElevated)
            .padding(12.dp)
    ) {
        Column {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextTertiary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            if (unit.isNotEmpty()) {
                Text(text = unit, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            }
        }
    }
}

@Composable
fun GoalItemRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    title: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(accentColor.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = title, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
        }

        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = TextSecondary
        )
    }
}

@Composable
fun EditProfileDialog(
    profile: com.example.data.model.UserProfileEntity,
    unit: UnitPreference,
    onDismiss: () -> Unit,
    onSave: (String, Int, Double, Double) -> Unit
) {
    var name by remember { mutableStateOf(profile.name) }
    var ageText by remember { mutableStateOf("${profile.age}") }
    var heightText by remember {
        mutableStateOf(
            if (unit == UnitPreference.METRIC) {
                String.format(Locale.US, "%.0f", profile.heightCm)
            } else {
                String.format(Locale.US, "%.1f", profile.heightCm / 2.54)
            }
        )
    }
    var weightText by remember {
        mutableStateOf(
            if (unit == UnitPreference.METRIC) {
                String.format(Locale.US, "%.1f", profile.weightKg)
            } else {
                String.format(Locale.US, "%.1f", profile.weightKg * 2.20462)
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profile & Metrics", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                OutlinedTextField(
                    value = ageText,
                    onValueChange = { ageText = it },
                    label = { Text("Age") },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                OutlinedTextField(
                    value = heightText,
                    onValueChange = { heightText = it },
                    label = { Text("Height (${UnitConverter.heightUnitLabel(unit)})") },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                OutlinedTextField(
                    value = weightText,
                    onValueChange = { weightText = it },
                    label = { Text("Weight (${UnitConverter.weightUnitLabel(unit)})") },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val age = ageText.toIntOrNull() ?: profile.age
                    val rawHeight = heightText.toDoubleOrNull() ?: profile.heightCm
                    val heightCm = if (unit == UnitPreference.METRIC) rawHeight else rawHeight * 2.54

                    val rawWeight = weightText.toDoubleOrNull() ?: profile.weightKg
                    val weightKg = if (unit == UnitPreference.METRIC) rawWeight else UnitConverter.lbsToKg(rawWeight)

                    onSave(name.ifBlank { profile.name }, age, heightCm, weightKg)
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen)
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

@Composable
fun EditGoalsDialog(
    profile: com.example.data.model.UserProfileEntity,
    onDismiss: () -> Unit,
    onSave: (Int, Int, Int, Int) -> Unit
) {
    var stepGoalText by remember { mutableStateOf("${profile.stepGoal}") }
    var calorieGoalText by remember { mutableStateOf("${profile.calorieGoal}") }
    var durationGoalText by remember { mutableStateOf("${profile.durationGoalMinutes}") }
    var waterGoalText by remember { mutableStateOf("${profile.waterGoalMl}") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Daily Goals", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = stepGoalText,
                    onValueChange = { stepGoalText = it },
                    label = { Text("Daily Steps Goal") },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                OutlinedTextField(
                    value = calorieGoalText,
                    onValueChange = { calorieGoalText = it },
                    label = { Text("Active Calories (kcal)") },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                OutlinedTextField(
                    value = durationGoalText,
                    onValueChange = { durationGoalText = it },
                    label = { Text("Workout Duration (mins)") },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                OutlinedTextField(
                    value = waterGoalText,
                    onValueChange = { waterGoalText = it },
                    label = { Text("Water Intake (ml)") },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val steps = stepGoalText.toIntOrNull() ?: profile.stepGoal
                    val calories = calorieGoalText.toIntOrNull() ?: profile.calorieGoal
                    val duration = durationGoalText.toIntOrNull() ?: profile.durationGoalMinutes
                    val water = waterGoalText.toIntOrNull() ?: profile.waterGoalMl
                    onSave(steps, calories, duration, water)
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen)
            ) {
                Text("Save Goals", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}
