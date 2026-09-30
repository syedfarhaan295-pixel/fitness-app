package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.example.data.model.ActiveExerciseLog
import com.example.data.model.Exercise
import com.example.data.model.UnitConverter
import com.example.data.model.WorkoutSet
import com.example.data.sample.PredefinedWorkouts
import com.example.ui.components.InteractiveRestTimerCard
import com.example.ui.theme.CalorieOrange
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DurationPurple
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonGreenLight
import com.example.ui.theme.StreakGold
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.viewmodel.FitPulseViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutLoggerScreen(
    viewModel: FitPulseViewModel,
    modifier: Modifier = Modifier
) {
    val activeSession by viewModel.activeWorkoutSession.collectAsStateWithLifecycle()
    val profile by viewModel.userProfile.collectAsStateWithLifecycle()
    val unit = profile.unitPrefEnum

    val restTimerRemaining by viewModel.restTimerSecondsRemaining.collectAsStateWithLifecycle()
    val restTimerTotal by viewModel.restTimerTotalSeconds.collectAsStateWithLifecycle()
    val isRestTimerRunning by viewModel.isRestTimerRunning.collectAsStateWithLifecycle()

    var showExercisePicker by remember { mutableStateOf(false) }
    var showFinishDialog by remember { mutableStateOf(false) }
    var showDiscardDialog by remember { mutableStateOf(false) }
    var showQuickAdjustModalForSet by remember { mutableStateOf<Pair<Int, Int>?>(null) } // (exerciseIndex, setIndex)

    if (activeSession == null) {
        // Empty State: Prompt to start a session
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp)
                .testTag("workout_logger_empty_state"),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(DarkSurfaceElevated, CircleShape)
                        .border(1.dp, DarkBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.FitnessCenter,
                        contentDescription = null,
                        tint = NeonGreen,
                        modifier = Modifier.size(42.dp)
                    )
                }

                Text(
                    text = "No Workout in Progress",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Text(
                    text = "Start a fresh workout session to log exercises, sets, weights, and track your rest intervals.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { viewModel.startBlankWorkout() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("start_quick_workout_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Start Quick Workout",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }
        return
    }

    val session = activeSession!!
    val elapsedMins = session.elapsedSeconds / 60
    val elapsedSecs = session.elapsedSeconds % 60
    val timerString = String.format(Locale.US, "%02d:%02d", elapsedMins, elapsedSecs)

    val totalSetsCount = session.exercises.sumOf { it.sets.size }
    val completedSetsCount = session.exercises.sumOf { it.sets.count { s -> s.isCompleted } }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("workout_logger_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header with active stopwatch and actions
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "ACTIVE WORKOUT",
                                style = MaterialTheme.typography.labelSmall,
                                letterSpacing = 1.2.sp,
                                color = NeonGreen
                            )
                            Text(
                                text = session.workoutTitle,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        // Elapsed stopwatch
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = DarkSurfaceElevated,
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                        ) {
                            Row(
                                modifier = Modifier
                                    .clickable { viewModel.toggleWorkoutPause() }
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (session.isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                                    contentDescription = "Toggle pause",
                                    tint = if (session.isPaused) CalorieOrange else NeonGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = timerString,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (session.isPaused) CalorieOrange else TextPrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Sets Completed & Finish Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Sets: $completedSetsCount of $totalSetsCount Completed",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextSecondary
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { showDiscardDialog = true },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF5252)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF5252).copy(alpha = 0.5f))
                            ) {
                                Text("Discard", style = MaterialTheme.typography.labelMedium)
                            }

                            Button(
                                onClick = { showFinishDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("finish_workout_btn")
                            ) {
                                Text(
                                    "Finish",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                    }
                }
            }
        }

        // Interactive Rest Timer Component
        item {
            InteractiveRestTimerCard(
                secondsRemaining = restTimerRemaining,
                totalSeconds = restTimerTotal,
                isRunning = isRestTimerRunning,
                onStartTimer = { viewModel.startRestTimer(it) },
                onPauseTimer = { viewModel.pauseRestTimer() },
                onResumeTimer = { viewModel.resumeRestTimer() },
                onAddSeconds = { viewModel.addRestTimerSeconds(it) },
                onResetTimer = { viewModel.resetRestTimer() }
            )
        }

        // Section Title: Exercises
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "EXERCISE LOG",
                    style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 1.2.sp,
                    color = TextSecondary
                )

                TextButton(
                    onClick = { showExercisePicker = true },
                    modifier = Modifier.testTag("add_exercise_header_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Exercise", color = ElectricCyan, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Exercise Cards
        itemsIndexed(session.exercises) { exIndex, exLog ->
            ExerciseLogCard(
                exerciseIndex = exIndex,
                exerciseLog = exLog,
                unit = unit,
                onAddSet = { viewModel.addSet(exIndex) },
                onRemoveSet = { setIndex -> viewModel.removeSet(exIndex, setIndex) },
                onToggleComplete = { setIndex -> viewModel.toggleSetCompleted(exIndex, setIndex) },
                onRemoveExercise = { viewModel.removeExerciseFromActiveWorkout(exIndex) },
                onAdjustWeight = { setIndex, delta -> viewModel.adjustSetWeight(exIndex, setIndex, delta) },
                onAdjustReps = { setIndex, delta -> viewModel.adjustSetReps(exIndex, setIndex, delta) },
                onOpenQuickEdit = { setIndex -> showQuickAdjustModalForSet = Pair(exIndex, setIndex) }
            )
        }

        // Bottom Add Exercise Button
        item {
            Button(
                onClick = { showExercisePicker = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("add_exercise_bottom_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceElevated),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.5f))
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = ElectricCyan)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Add Next Exercise",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = ElectricCyan
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Modal Sheet for Exercise Search & Selection
    if (showExercisePicker) {
        ExercisePickerBottomSheet(
            onDismiss = { showExercisePicker = false },
            onSelectExercise = { exercise ->
                viewModel.addExerciseToActiveWorkout(exercise)
                showExercisePicker = false
            }
        )
    }

    // Finish Workout Dialog
    if (showFinishDialog) {
        AlertDialog(
            onDismissRequest = { showFinishDialog = false },
            title = { Text("Complete Workout?", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Great session! Ready to log this workout into your progress history?")
                    Text("• Time: $timerString", color = TextSecondary)
                    Text("• Sets Completed: $completedSetsCount", color = TextSecondary)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.finishWorkout()
                        showFinishDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreen)
                ) {
                    Text("Save & Log", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showFinishDialog = false }) {
                    Text("Keep Going", color = TextSecondary)
                }
            }
        )
    }

    // Discard Workout Dialog
    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = { Text("Discard Workout?") },
            text = { Text("Are you sure you want to discard this workout? Current session data will not be saved.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.discardWorkout()
                        showDiscardDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252))
                ) {
                    Text("Discard", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }

    // Quick Adjust Modal for Set
    showQuickAdjustModalForSet?.let { (exIdx, setIdx) ->
        val currentEx = session.exercises.getOrNull(exIdx)
        val currentSet = currentEx?.sets?.getOrNull(setIdx)
        if (currentSet != null) {
            SetQuickAdjustDialog(
                setNumber = currentSet.setNumber,
                currentWeight = currentSet.weightKg,
                currentReps = currentSet.reps,
                unit = unit,
                onDismiss = { showQuickAdjustModalForSet = null },
                onSave = { newWeight, newReps ->
                    viewModel.updateSet(exIdx, setIdx, newWeight, newReps)
                    showQuickAdjustModalForSet = null
                }
            )
        }
    }
}

@Composable
fun ExerciseLogCard(
    exerciseIndex: Int,
    exerciseLog: ActiveExerciseLog,
    unit: com.example.data.model.UnitPreference,
    onAddSet: () -> Unit,
    onRemoveSet: (Int) -> Unit,
    onToggleComplete: (Int) -> Unit,
    onRemoveExercise: () -> Unit,
    onAdjustWeight: (Int, Double) -> Unit,
    onAdjustReps: (Int, Int) -> Unit,
    onOpenQuickEdit: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("exercise_log_card_$exerciseIndex"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Exercise Title & Remove button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = exerciseLog.exerciseName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = exerciseLog.muscleGroup,
                        style = MaterialTheme.typography.labelSmall,
                        color = ElectricCyan
                    )
                }

                IconButton(
                    onClick = onRemoveExercise,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove exercise",
                        tint = TextTertiary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Table Header: SET, PREV, WEIGHT, REPS, COMPLETE
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SET",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary,
                    modifier = Modifier.width(36.dp)
                )
                Text(
                    text = "PREV",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = UnitConverter.weightUnitLabel(unit).uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary,
                    modifier = Modifier.weight(1.3f)
                )
                Text(
                    text = "REPS",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary,
                    modifier = Modifier.weight(1.2f)
                )
                Text(
                    text = "DONE",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary,
                    modifier = Modifier.width(42.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }

            // Sets list
            exerciseLog.sets.forEachIndexed { setIdx, setItem ->
                SetRowItem(
                    setItem = setItem,
                    unit = unit,
                    onToggleComplete = { onToggleComplete(setIdx) },
                    onAdjustWeight = { delta -> onAdjustWeight(setIdx, delta) },
                    onAdjustReps = { delta -> onAdjustReps(setIdx, delta) },
                    onOpenQuickEdit = { onOpenQuickEdit(setIdx) },
                    onDelete = { onRemoveSet(setIdx) },
                    canDelete = exerciseLog.sets.size > 1
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Add Set Button
            TextButton(
                onClick = onAddSet,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("add_set_ex_${exerciseIndex}_btn")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("+ Add Set", color = NeonGreen, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SetRowItem(
    setItem: WorkoutSet,
    unit: com.example.data.model.UnitPreference,
    onToggleComplete: () -> Unit,
    onAdjustWeight: (Double) -> Unit,
    onAdjustReps: (Int) -> Unit,
    onOpenQuickEdit: () -> Unit,
    onDelete: () -> Unit,
    canDelete: Boolean
) {
    val displayWeight = if (unit == com.example.data.model.UnitPreference.METRIC) {
        String.format(Locale.US, "%.1f", setItem.weightKg)
    } else {
        String.format(Locale.US, "%.1f", UnitConverter.kgToLbs(setItem.weightKg))
    }

    val rowBg by animateColorAsState(
        targetValue = if (setItem.isCompleted) NeonGreen.copy(alpha = 0.12f) else DarkSurfaceElevated,
        label = "setBg"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(rowBg)
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .testTag("set_row_${setItem.setNumber}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Set Number
        Text(
            text = "${setItem.setNumber}",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = if (setItem.isCompleted) NeonGreen else TextPrimary,
            modifier = Modifier.width(36.dp)
        )

        // Previous
        Text(
            text = setItem.previous,
            style = MaterialTheme.typography.bodySmall,
            color = TextTertiary,
            modifier = Modifier.weight(1f)
        )

        // Weight with increment buttons
        Row(
            modifier = Modifier
                .weight(1.3f)
                .clickable { onOpenQuickEdit() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = displayWeight,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.width(4.dp))
            // Quick small plus/minus buttons
            Column {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "+2.5kg",
                    tint = TextSecondary,
                    modifier = Modifier
                        .size(16.dp)
                        .clickable { onAdjustWeight(if (unit == com.example.data.model.UnitPreference.METRIC) 2.5 else 1.13) }
                )
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "-2.5kg",
                    tint = TextSecondary,
                    modifier = Modifier
                        .size(16.dp)
                        .clickable { onAdjustWeight(if (unit == com.example.data.model.UnitPreference.METRIC) -2.5 else -1.13) }
                )
            }
        }

        // Reps with increment buttons
        Row(
            modifier = Modifier
                .weight(1.2f)
                .clickable { onOpenQuickEdit() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${setItem.reps}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "+1 rep",
                    tint = TextSecondary,
                    modifier = Modifier
                        .size(16.dp)
                        .clickable { onAdjustReps(1) }
                )
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "-1 rep",
                    tint = TextSecondary,
                    modifier = Modifier
                        .size(16.dp)
                        .clickable { onAdjustReps(-1) }
                )
            }
        }

        // Completion Checkbox
        Box(
            modifier = Modifier
                .width(42.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                onClick = onToggleComplete,
                shape = RoundedCornerShape(8.dp),
                color = if (setItem.isCompleted) NeonGreen else DarkBorder,
                modifier = Modifier
                    .size(32.dp)
                    .testTag("set_checkbox_${setItem.setNumber}")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (setItem.isCompleted) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Completed set",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExercisePickerBottomSheet(
    onDismiss: () -> Unit,
    onSelectExercise: (Exercise) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var searchQuery by remember { mutableStateOf("") }
    var selectedMuscleGroup by remember { mutableStateOf("All") }
    var customExerciseName by remember { mutableStateOf("") }
    var showCustomInput by remember { mutableStateOf(false) }

    val muscleGroups = listOf("All", "Chest", "Back", "Legs", "Shoulders", "Arms", "Core", "Full Body")

    val filteredList = PredefinedWorkouts.exerciseLibrary.filter { ex ->
        val matchesQuery = searchQuery.isBlank() || ex.name.contains(searchQuery, ignoreCase = true)
        val matchesGroup = (selectedMuscleGroup == "All" || ex.muscleGroup.equals(selectedMuscleGroup, ignoreCase = true))
        matchesQuery && matchesGroup
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("exercise_picker_sheet")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Select Exercise",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("exercise_search_input"),
                placeholder = { Text("Search by exercise name...", color = TextTertiary) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ElectricCyan,
                    unfocusedBorderColor = DarkBorder,
                    focusedContainerColor = DarkSurfaceElevated,
                    unfocusedContainerColor = DarkSurfaceElevated
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Muscle group chips
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(muscleGroups) { group ->
                    val isSelected = group == selectedMuscleGroup
                    Surface(
                        onClick = { selectedMuscleGroup = group },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) ElectricCyan.copy(alpha = 0.2f) else DarkSurfaceElevated,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) ElectricCyan else DarkBorder
                        )
                    ) {
                        Text(
                            text = group,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) ElectricCyan else TextSecondary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Custom exercise creator option
            if (showCustomInput) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = customExerciseName,
                        onValueChange = { customExerciseName = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Custom exercise name", color = TextTertiary) },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    Button(
                        onClick = {
                            if (customExerciseName.isNotBlank()) {
                                onSelectExercise(
                                    Exercise(
                                        id = "custom_${System.currentTimeMillis()}",
                                        name = customExerciseName.trim(),
                                        category = "Strength",
                                        muscleGroup = "Full Body",
                                        equipment = "Custom",
                                        defaultSets = 3,
                                        defaultReps = 10,
                                        defaultWeightKg = 20.0
                                    )
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Add", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            } else {
                TextButton(
                    onClick = { showCustomInput = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Create Custom Exercise", color = NeonGreen)
                }
            }

            // Exercise list
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredList) { exercise ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectExercise(exercise) }
                            .testTag("exercise_picker_item_${exercise.id}"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = exercise.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "${exercise.muscleGroup} • ${exercise.equipment}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Select",
                                tint = ElectricCyan
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SetQuickAdjustDialog(
    setNumber: Int,
    currentWeight: Double,
    currentReps: Int,
    unit: com.example.data.model.UnitPreference,
    onDismiss: () -> Unit,
    onSave: (Double, Int) -> Unit
) {
    var weightText by remember {
        mutableStateOf(
            if (unit == com.example.data.model.UnitPreference.METRIC) {
                String.format(Locale.US, "%.1f", currentWeight)
            } else {
                String.format(Locale.US, "%.1f", UnitConverter.kgToLbs(currentWeight))
            }
        )
    }
    var repsText by remember { mutableStateOf("$currentReps") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Set #$setNumber", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = weightText,
                    onValueChange = { weightText = it },
                    label = { Text("Weight (${UnitConverter.weightUnitLabel(unit)})") },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                OutlinedTextField(
                    value = repsText,
                    onValueChange = { repsText = it },
                    label = { Text("Reps") },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsedWeight = weightText.toDoubleOrNull() ?: currentWeight
                    val weightKg = if (unit == com.example.data.model.UnitPreference.METRIC) {
                        parsedWeight
                    } else {
                        UnitConverter.lbsToKg(parsedWeight)
                    }
                    val parsedReps = repsText.toIntOrNull() ?: currentReps
                    onSave(weightKg, parsedReps)
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
