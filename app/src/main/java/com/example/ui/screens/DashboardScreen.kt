package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.UnitConverter
import com.example.data.model.WorkoutPlan
import com.example.data.sample.PredefinedWorkouts
import com.example.ui.components.MultiActivityRings
import com.example.ui.theme.CalorieOrange
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DurationPurple
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonGreenDark
import com.example.ui.theme.StreakGold
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.theme.WaterBlue
import com.example.viewmodel.FitPulseViewModel
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: FitPulseViewModel,
    onNavigateToLogger: () -> Unit,
    onNavigateToPlanner: () -> Unit,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.userProfile.collectAsStateWithLifecycle()
    val todayActivity by viewModel.todayActivity.collectAsStateWithLifecycle()
    val activeSession by viewModel.activeWorkoutSession.collectAsStateWithLifecycle()

    val stepProgress = (todayActivity.steps.toFloat() / profile.stepGoal.toFloat()).coerceAtLeast(0f)
    val calorieProgress = (todayActivity.caloriesBurned.toFloat() / profile.calorieGoal.toFloat()).coerceAtLeast(0f)
    val durationProgress = (todayActivity.workoutDurationMinutes.toFloat() / profile.durationGoalMinutes.toFloat()).coerceAtLeast(0f)
    val waterProgress = (todayActivity.waterIntakeMl.toFloat() / profile.waterGoalMl.toFloat()).coerceIn(0f, 1f)

    val recommendedPlan: WorkoutPlan = PredefinedWorkouts.sampleWorkoutPlans.first()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("dashboard_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // User Greeting & Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "WELCOME BACK,",
                        style = MaterialTheme.typography.labelSmall,
                        letterSpacing = 1.2.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = profile.name,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                // Active Streak Badge
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = StreakGold.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, StreakGold.copy(alpha = 0.4f)),
                    modifier = Modifier.testTag("streak_badge")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = "Streak flame",
                            tint = StreakGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "14 DAYS",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = StreakGold
                        )
                    }
                }
            }
        }

        // Active workout banner if one is running
        if (activeSession != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToLogger() }
                        .testTag("active_workout_ongoing_card"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = NeonGreenDark.copy(alpha = 0.2f)),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, NeonGreen)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(NeonGreen, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FitnessCenter,
                                    contentDescription = "Active workout",
                                    tint = Color.Black,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "WORKOUT IN PROGRESS",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NeonGreen,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = activeSession?.workoutTitle ?: "Workout",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                        }

                        Button(
                            onClick = onNavigateToLogger,
                            colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Resume", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Concentric Rings Summary Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("rings_summary_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "TODAY'S ACTIVITY",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Visual Multi-Ring
                        MultiActivityRings(
                            stepsProgress = stepProgress,
                            caloriesProgress = calorieProgress,
                            durationProgress = durationProgress,
                            size = 150.dp,
                            strokeWidth = 11.dp
                        )

                        // Legend / Values
                        Column(
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier.padding(start = 16.dp)
                        ) {
                            // Steps Legend
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .background(NeonGreen, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "${todayActivity.steps} / ${profile.stepGoal}",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text("Steps", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                                }
                            }

                            // Calories Legend
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .background(CalorieOrange, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "${todayActivity.caloriesBurned} / ${profile.calorieGoal} kcal",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text("Active Burn", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                                }
                            }

                            // Duration Legend
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .background(DurationPurple, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "${todayActivity.workoutDurationMinutes} / ${profile.durationGoalMinutes} min",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text("Exercise", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2x2 Activity Meters Grid (Steps, Calories, Duration, Water)
        item {
            Text(
                text = "METRICS BREAKDOWN",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Steps Card
                    MetricCard(
                        title = "Steps",
                        current = String.format(Locale.US, "%,d", todayActivity.steps),
                        goal = String.format(Locale.US, "%,d", profile.stepGoal),
                        progress = stepProgress,
                        accentColor = NeonGreen,
                        icon = Icons.Default.DirectionsRun,
                        actionLabel = "+1,000",
                        onAction = { viewModel.logSteps(1000) },
                        modifier = Modifier.weight(1f)
                    )

                    // Calories Card
                    MetricCard(
                        title = "Calories",
                        current = "${todayActivity.caloriesBurned}",
                        goal = "${profile.calorieGoal} kcal",
                        progress = calorieProgress,
                        accentColor = CalorieOrange,
                        icon = Icons.Default.LocalFireDepartment,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Duration Card
                    MetricCard(
                        title = "Workout",
                        current = "${todayActivity.workoutDurationMinutes}m",
                        goal = "${profile.durationGoalMinutes}m target",
                        progress = durationProgress,
                        accentColor = DurationPurple,
                        icon = Icons.Default.Timer,
                        modifier = Modifier.weight(1f)
                    )

                    // Water Intake Card with quick add/remove
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("metric_card_water"),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Opacity,
                                        contentDescription = "Water",
                                        tint = WaterBlue,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Water",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = TextSecondary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = UnitConverter.formatWater(todayActivity.waterIntakeMl, profile.unitPrefEnum),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Goal: ${UnitConverter.formatWater(profile.waterGoalMl, profile.unitPrefEnum)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextTertiary
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            LinearProgressIndicator(
                                progress = { waterProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = WaterBlue,
                                trackColor = DarkBorder,
                                strokeCap = StrokeCap.Round
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                            // Quick buttons +250ml / -250ml
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Surface(
                                    onClick = { viewModel.logWater(-250) },
                                    shape = RoundedCornerShape(8.dp),
                                    color = DarkBorder.copy(alpha = 0.5f),
                                    modifier = Modifier.testTag("water_minus_btn")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.Remove,
                                            contentDescription = "Minus water",
                                            tint = TextSecondary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text("250", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                                    }
                                }

                                Surface(
                                    onClick = { viewModel.logWater(250) },
                                    shape = RoundedCornerShape(8.dp),
                                    color = WaterBlue.copy(alpha = 0.2f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, WaterBlue.copy(alpha = 0.4f)),
                                    modifier = Modifier.testTag("water_plus_btn")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.Add,
                                            contentDescription = "Add water",
                                            tint = WaterBlue,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text("+250", style = MaterialTheme.typography.labelSmall, color = WaterBlue)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Today's Recommended Workout Hero Card
        item {
            Text(
                text = "DAILY RECOMMENDED WORKOUT",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("recommended_workout_hero"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    // Hero Image Banner
                    Image(
                        painter = painterResource(id = R.drawable.hero_workout_banner),
                        contentDescription = "Workout Hero Banner",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentScale = ContentScale.Crop
                    )

                    // Gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, DarkSurface.copy(alpha = 0.95f), DarkSurface)
                                )
                            )
                    )

                    // Content on top of banner
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        // Badges
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = NeonGreen.copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, NeonGreen)
                            ) {
                                Text(
                                    text = recommendedPlan.intensity.label.uppercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonGreen,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = ElectricCyan.copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, ElectricCyan)
                            ) {
                                Text(
                                    text = recommendedPlan.focusArea.label.uppercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = ElectricCyan,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(36.dp))

                        Text(
                            text = recommendedPlan.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "${recommendedPlan.estimatedMinutes} mins • ~${recommendedPlan.estimatedCalories} kcal • ${recommendedPlan.exercises.size} Exercises",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    viewModel.startWorkoutFromPlan(recommendedPlan)
                                    onNavigateToLogger()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("start_recommended_workout_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "Start Workout",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }

                            Button(
                                onClick = onNavigateToPlanner,
                                modifier = Modifier
                                    .height(48.dp)
                                    .testTag("view_all_plans_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceElevated),
                                shape = RoundedCornerShape(14.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                            ) {
                                Text("All Plans", color = TextPrimary)
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    current: String,
    goal: String,
    progress: Float,
    accentColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    Card(
        modifier = modifier.testTag("metric_card_${title.lowercase()}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary
                    )
                }

                if (actionLabel != null && onAction != null) {
                    Surface(
                        onClick = onAction,
                        shape = RoundedCornerShape(8.dp),
                        color = accentColor.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.35f))
                    ) {
                        Text(
                            text = actionLabel,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = accentColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = current,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Goal: $goal",
                style = MaterialTheme.typography.labelSmall,
                color = TextTertiary
            )

            Spacer(modifier = Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = accentColor,
                trackColor = DarkBorder,
                strokeCap = StrokeCap.Round
            )
        }
    }
}
