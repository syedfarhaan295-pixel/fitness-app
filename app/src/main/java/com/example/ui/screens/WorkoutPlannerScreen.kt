package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.data.model.FocusArea
import com.example.data.model.WorkoutIntensity
import com.example.data.model.WorkoutPlan
import com.example.ui.theme.CalorieOrange
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DurationPurple
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.viewmodel.FitPulseViewModel

@Composable
fun WorkoutPlannerScreen(
    viewModel: FitPulseViewModel,
    onNavigateToLogger: () -> Unit,
    modifier: Modifier = Modifier
) {
    val plans by viewModel.filteredWorkoutPlans.collectAsStateWithLifecycle()
    val selectedIntensity by viewModel.selectedIntensity.collectAsStateWithLifecycle()
    val selectedFocus by viewModel.selectedFocus.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    var expandedPlanId by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("workout_planner_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "WORKOUT PLANNER",
                style = MaterialTheme.typography.labelSmall,
                letterSpacing = 1.2.sp,
                color = TextSecondary
            )
            Text(
                text = "Curated Routines",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        // Search bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("planner_search_input"),
                placeholder = { Text("Search workouts or exercises...", color = TextTertiary) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = TextSecondary
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear search", tint = TextSecondary)
                        }
                    }
                },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ElectricCyan,
                    unfocusedBorderColor = DarkBorder,
                    focusedContainerColor = DarkSurfaceElevated,
                    unfocusedContainerColor = DarkSurface
                ),
                singleLine = true
            )
        }

        // Focus Area Filter Chips
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "FOCUS AREA",
                    style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 1.sp,
                    color = TextSecondary
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FocusArea.values().forEach { focus ->
                        val isSelected = (focus == selectedFocus)
                        Surface(
                            onClick = { viewModel.setFocusFilter(focus) },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) ElectricCyan.copy(alpha = 0.2f) else DarkSurface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) ElectricCyan else DarkBorder
                            ),
                            modifier = Modifier.testTag("filter_focus_${focus.name.lowercase()}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = focus.label,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) ElectricCyan else TextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Intensity Filter Chips
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "INTENSITY LEVEL",
                    style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 1.sp,
                    color = TextSecondary
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    WorkoutIntensity.values().forEach { intensity ->
                        val isSelected = (intensity == selectedIntensity)
                        Surface(
                            onClick = { viewModel.setIntensityFilter(intensity) },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) NeonGreen.copy(alpha = 0.2f) else DarkSurface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) NeonGreen else DarkBorder
                            ),
                            modifier = Modifier.testTag("filter_intensity_${intensity.name.lowercase()}")
                        ) {
                            Text(
                                text = intensity.label,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) NeonGreen else TextSecondary,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        // Workout count header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${plans.size} Workouts Found",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextSecondary
                )
            }
        }

        // Workout plans list
        if (plans.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.FitnessCenter,
                            contentDescription = null,
                            tint = TextTertiary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No matching workouts found",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }
            }
        } else {
            items(plans, key = { it.id }) { plan ->
                val isExpanded = (expandedPlanId == plan.id)
                WorkoutPlanCard(
                    plan = plan,
                    isExpanded = isExpanded,
                    onToggleExpand = {
                        expandedPlanId = if (isExpanded) null else plan.id
                    },
                    onStartWorkout = {
                        viewModel.startWorkoutFromPlan(plan)
                        onNavigateToLogger()
                    }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun WorkoutPlanCard(
    plan: WorkoutPlan,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onStartWorkout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("workout_card_${plan.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header: Badges & Expand icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val intensityColor = when (plan.intensity) {
                        WorkoutIntensity.BEGINNER -> NeonGreen
                        WorkoutIntensity.INTERMEDIATE -> ElectricCyan
                        WorkoutIntensity.ADVANCED -> CalorieOrange
                        else -> TextSecondary
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = intensityColor.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, intensityColor.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = plan.intensity.label,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = intensityColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = DarkBorder.copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = plan.focusArea.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                IconButton(
                    onClick = onToggleExpand,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Expand workout",
                        tint = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title & Description
            Text(
                text = plan.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = plan.description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                maxLines = if (isExpanded) Int.MAX_VALUE else 2
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Meta Row (Duration, Calories, Exercises)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        tint = DurationPurple,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${plan.estimatedMinutes} min",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextPrimary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = CalorieOrange,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "~${plan.estimatedCalories} kcal",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextPrimary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FitnessCenter,
                        contentDescription = null,
                        tint = ElectricCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${plan.exercises.size} Exercises",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextPrimary
                    )
                }
            }

            // Expanded Exercises details
            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp)
                ) {
                    Text(
                        text = "EXERCISES INCLUDED",
                        style = MaterialTheme.typography.labelSmall,
                        letterSpacing = 1.sp,
                        color = TextTertiary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    plan.exercises.forEachIndexed { idx, ex ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${idx + 1}.",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = ElectricCyan,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.width(22.dp)
                                )
                                Column {
                                    Text(
                                        text = ex.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "${ex.muscleGroup} • ${ex.equipment}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextTertiary
                                    )
                                }
                            }
                            Text(
                                text = "${ex.defaultSets} × ${ex.defaultReps}",
                                style = MaterialTheme.typography.labelMedium,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Button
            Button(
                onClick = onStartWorkout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("start_plan_${plan.id}_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Start Workout",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}
