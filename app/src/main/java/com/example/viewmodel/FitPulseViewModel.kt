package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.FitPulseDatabase
import com.example.data.model.ActiveExerciseLog
import com.example.data.model.ActiveWorkoutSession
import com.example.data.model.DailyActivityEntity
import com.example.data.model.Exercise
import com.example.data.model.FocusArea
import com.example.data.model.UnitConverter
import com.example.data.model.UnitPreference
import com.example.data.model.UserProfileEntity
import com.example.data.model.WeightEntryEntity
import com.example.data.model.WorkoutIntensity
import com.example.data.model.WorkoutPlan
import com.example.data.model.WorkoutSessionEntity
import com.example.data.model.WorkoutSet
import com.example.data.repository.FitPulseRepository
import com.example.data.sample.PredefinedWorkouts
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FitPulseViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FitPulseRepository

    init {
        val db = FitPulseDatabase.getDatabase(application, viewModelScope)
        repository = FitPulseRepository(db.fitPulseDao())
    }

    val userProfile: StateFlow<UserProfileEntity> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProfileEntity())

    val todayActivity: StateFlow<DailyActivityEntity> = repository.todayActivity
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            DailyActivityEntity(date = UnitConverter.getTodayDateKey())
        )

    val weightEntries: StateFlow<List<WeightEntryEntity>> = repository.weightEntries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val workoutSessions: StateFlow<List<WorkoutSessionEntity>> = repository.workoutSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Workout Planner Filtering ---
    private val _selectedIntensity = MutableStateFlow(WorkoutIntensity.ALL)
    val selectedIntensity: StateFlow<WorkoutIntensity> = _selectedIntensity.asStateFlow()

    private val _selectedFocus = MutableStateFlow(FocusArea.ALL)
    val selectedFocus: StateFlow<FocusArea> = _selectedFocus.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val filteredWorkoutPlans: StateFlow<List<WorkoutPlan>> = combine(
        _selectedIntensity,
        _selectedFocus,
        _searchQuery
    ) { intensity, focus, query ->
        PredefinedWorkouts.sampleWorkoutPlans.filter { plan ->
            val matchIntensity = (intensity == WorkoutIntensity.ALL || plan.intensity == intensity)
            val matchFocus = (focus == FocusArea.ALL || plan.focusArea == focus)
            val matchQuery = query.isBlank() ||
                    plan.title.contains(query, ignoreCase = true) ||
                    plan.description.contains(query, ignoreCase = true) ||
                    plan.exercises.any { it.name.contains(query, ignoreCase = true) }
            matchIntensity && matchFocus && matchQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PredefinedWorkouts.sampleWorkoutPlans)

    fun setIntensityFilter(intensity: WorkoutIntensity) {
        _selectedIntensity.value = intensity
    }

    fun setFocusFilter(focus: FocusArea) {
        _selectedFocus.value = focus
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // --- Interactive Workout Logger (Option B) ---
    private val _activeWorkoutSession = MutableStateFlow<ActiveWorkoutSession?>(null)
    val activeWorkoutSession: StateFlow<ActiveWorkoutSession?> = _activeWorkoutSession.asStateFlow()

    private var workoutTimerJob: Job? = null

    // Rest Timer state
    private val _restTimerSecondsRemaining = MutableStateFlow(0)
    val restTimerSecondsRemaining: StateFlow<Int> = _restTimerSecondsRemaining.asStateFlow()

    private val _restTimerTotalSeconds = MutableStateFlow(60)
    val restTimerTotalSeconds: StateFlow<Int> = _restTimerTotalSeconds.asStateFlow()

    private val _isRestTimerRunning = MutableStateFlow(false)
    val isRestTimerRunning: StateFlow<Boolean> = _isRestTimerRunning.asStateFlow()

    private var restTimerJob: Job? = null

    fun startWorkoutFromPlan(plan: WorkoutPlan) {
        val exerciseLogs = plan.exercises.map { exercise ->
            val initialSets = (1..exercise.defaultSets).map { setNum ->
                WorkoutSet(
                    setNumber = setNum,
                    previous = "${exercise.defaultWeightKg.toInt()} kg × ${exercise.defaultReps}",
                    weightKg = exercise.defaultWeightKg,
                    reps = exercise.defaultReps,
                    isCompleted = false
                )
            }
            ActiveExerciseLog(
                exerciseId = exercise.id,
                exerciseName = exercise.name,
                muscleGroup = exercise.muscleGroup,
                sets = initialSets
            )
        }

        _activeWorkoutSession.value = ActiveWorkoutSession(
            workoutTitle = plan.title,
            focusArea = plan.focusArea.name,
            intensity = plan.intensity.name,
            startTime = System.currentTimeMillis(),
            elapsedSeconds = 0,
            isPaused = false,
            exercises = exerciseLogs
        )
        startWorkoutTimer()
    }

    fun startBlankWorkout() {
        val defaultEx = PredefinedWorkouts.exerciseLibrary.first()
        _activeWorkoutSession.value = ActiveWorkoutSession(
            workoutTitle = "Custom Workout",
            focusArea = "Strength",
            intensity = "Intermediate",
            startTime = System.currentTimeMillis(),
            elapsedSeconds = 0,
            isPaused = false,
            exercises = listOf(
                ActiveExerciseLog(
                    exerciseId = defaultEx.id,
                    exerciseName = defaultEx.name,
                    muscleGroup = defaultEx.muscleGroup,
                    sets = listOf(
                        WorkoutSet(1, "—", defaultEx.defaultWeightKg, defaultEx.defaultReps, false),
                        WorkoutSet(2, "—", defaultEx.defaultWeightKg, defaultEx.defaultReps, false),
                        WorkoutSet(3, "—", defaultEx.defaultWeightKg, defaultEx.defaultReps, false)
                    )
                )
            )
        )
        startWorkoutTimer()
    }

    private fun startWorkoutTimer() {
        workoutTimerJob?.cancel()
        workoutTimerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _activeWorkoutSession.value?.let { current ->
                    if (!current.isPaused) {
                        _activeWorkoutSession.value = current.copy(
                            elapsedSeconds = current.elapsedSeconds + 1
                        )
                    }
                }
            }
        }
    }

    fun toggleWorkoutPause() {
        _activeWorkoutSession.value?.let { session ->
            _activeWorkoutSession.value = session.copy(isPaused = !session.isPaused)
        }
    }

    fun addExerciseToActiveWorkout(exercise: Exercise) {
        val current = _activeWorkoutSession.value ?: return
        val newExerciseLog = ActiveExerciseLog(
            exerciseId = exercise.id,
            exerciseName = exercise.name,
            muscleGroup = exercise.muscleGroup,
            sets = listOf(
                WorkoutSet(1, "—", exercise.defaultWeightKg, exercise.defaultReps, false),
                WorkoutSet(2, "—", exercise.defaultWeightKg, exercise.defaultReps, false),
                WorkoutSet(3, "—", exercise.defaultWeightKg, exercise.defaultReps, false)
            )
        )
        _activeWorkoutSession.value = current.copy(
            exercises = current.exercises + newExerciseLog
        )
    }

    fun removeExerciseFromActiveWorkout(exerciseIndex: Int) {
        val current = _activeWorkoutSession.value ?: return
        if (exerciseIndex in current.exercises.indices) {
            val updated = current.exercises.toMutableList().apply { removeAt(exerciseIndex) }
            _activeWorkoutSession.value = current.copy(exercises = updated)
        }
    }

    fun addSet(exerciseIndex: Int) {
        val current = _activeWorkoutSession.value ?: return
        if (exerciseIndex in current.exercises.indices) {
            val exLog = current.exercises[exerciseIndex]
            val lastSet = exLog.sets.lastOrNull()
            val newSet = WorkoutSet(
                setNumber = exLog.sets.size + 1,
                previous = lastSet?.let { "${it.weightKg.toInt()} kg × ${it.reps}" } ?: "—",
                weightKg = lastSet?.weightKg ?: 20.0,
                reps = lastSet?.reps ?: 10,
                isCompleted = false
            )
            val updatedSets = exLog.sets + newSet
            val updatedExercises = current.exercises.toMutableList()
            updatedExercises[exerciseIndex] = exLog.copy(sets = updatedSets)
            _activeWorkoutSession.value = current.copy(exercises = updatedExercises)
        }
    }

    fun removeSet(exerciseIndex: Int, setIndex: Int) {
        val current = _activeWorkoutSession.value ?: return
        if (exerciseIndex in current.exercises.indices) {
            val exLog = current.exercises[exerciseIndex]
            if (setIndex in exLog.sets.indices && exLog.sets.size > 1) {
                val updatedSets = exLog.sets.toMutableList().apply { removeAt(setIndex) }
                    .mapIndexed { idx, s -> s.copy(setNumber = idx + 1) }
                val updatedExercises = current.exercises.toMutableList()
                updatedExercises[exerciseIndex] = exLog.copy(sets = updatedSets)
                _activeWorkoutSession.value = current.copy(exercises = updatedExercises)
            }
        }
    }

    fun updateSet(exerciseIndex: Int, setIndex: Int, weightKg: Double, reps: Int) {
        val current = _activeWorkoutSession.value ?: return
        if (exerciseIndex in current.exercises.indices) {
            val exLog = current.exercises[exerciseIndex]
            if (setIndex in exLog.sets.indices) {
                val updatedSets = exLog.sets.toMutableList()
                val oldSet = updatedSets[setIndex]
                updatedSets[setIndex] = oldSet.copy(weightKg = weightKg.coerceAtLeast(0.0), reps = reps.coerceAtLeast(1))
                val updatedExercises = current.exercises.toMutableList()
                updatedExercises[exerciseIndex] = exLog.copy(sets = updatedSets)
                _activeWorkoutSession.value = current.copy(exercises = updatedExercises)
            }
        }
    }

    fun adjustSetWeight(exerciseIndex: Int, setIndex: Int, deltaKg: Double) {
        val current = _activeWorkoutSession.value ?: return
        if (exerciseIndex in current.exercises.indices) {
            val exLog = current.exercises[exerciseIndex]
            if (setIndex in exLog.sets.indices) {
                val oldSet = exLog.sets[setIndex]
                val newWeight = (oldSet.weightKg + deltaKg).coerceAtLeast(0.0)
                updateSet(exerciseIndex, setIndex, newWeight, oldSet.reps)
            }
        }
    }

    fun adjustSetReps(exerciseIndex: Int, setIndex: Int, deltaReps: Int) {
        val current = _activeWorkoutSession.value ?: return
        if (exerciseIndex in current.exercises.indices) {
            val exLog = current.exercises[exerciseIndex]
            if (setIndex in exLog.sets.indices) {
                val oldSet = exLog.sets[setIndex]
                val newReps = (oldSet.reps + deltaReps).coerceAtLeast(1)
                updateSet(exerciseIndex, setIndex, oldSet.weightKg, newReps)
            }
        }
    }

    fun toggleSetCompleted(exerciseIndex: Int, setIndex: Int, autoStartRestTimer: Boolean = true) {
        val current = _activeWorkoutSession.value ?: return
        if (exerciseIndex in current.exercises.indices) {
            val exLog = current.exercises[exerciseIndex]
            if (setIndex in exLog.sets.indices) {
                val targetSet = exLog.sets[setIndex]
                val newCompleted = !targetSet.isCompleted
                val updatedSets = exLog.sets.toMutableList()
                updatedSets[setIndex] = targetSet.copy(isCompleted = newCompleted)
                val updatedExercises = current.exercises.toMutableList()
                updatedExercises[exerciseIndex] = exLog.copy(sets = updatedSets)
                _activeWorkoutSession.value = current.copy(exercises = updatedExercises)

                if (newCompleted && autoStartRestTimer) {
                    startRestTimer(_restTimerTotalSeconds.value)
                }
            }
        }
    }

    // --- Rest Timer implementation ---
    fun startRestTimer(seconds: Int) {
        restTimerJob?.cancel()
        _restTimerTotalSeconds.value = seconds
        _restTimerSecondsRemaining.value = seconds
        _isRestTimerRunning.value = true

        restTimerJob = viewModelScope.launch {
            while (_restTimerSecondsRemaining.value > 0) {
                delay(1000)
                _restTimerSecondsRemaining.value -= 1
            }
            _isRestTimerRunning.value = false
            triggerVibration()
        }
    }

    fun pauseRestTimer() {
        restTimerJob?.cancel()
        _isRestTimerRunning.value = false
    }

    fun resumeRestTimer() {
        if (_restTimerSecondsRemaining.value > 0) {
            _isRestTimerRunning.value = true
            restTimerJob = viewModelScope.launch {
                while (_restTimerSecondsRemaining.value > 0) {
                    delay(1000)
                    _restTimerSecondsRemaining.value -= 1
                }
                _isRestTimerRunning.value = false
                triggerVibration()
            }
        }
    }

    fun addRestTimerSeconds(seconds: Int) {
        _restTimerSecondsRemaining.value += seconds
        _restTimerTotalSeconds.value = maxOf(_restTimerTotalSeconds.value, _restTimerSecondsRemaining.value)
        if (!_isRestTimerRunning.value && _restTimerSecondsRemaining.value > 0) {
            resumeRestTimer()
        }
    }

    fun resetRestTimer() {
        restTimerJob?.cancel()
        _restTimerSecondsRemaining.value = 0
        _isRestTimerRunning.value = false
    }

    private fun triggerVibration() {
        try {
            val app = getApplication<Application>()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = app.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createWaveform(longArrayOf(0, 150, 100, 250), -1)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = app.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(
                        VibrationEffect.createWaveform(longArrayOf(0, 150, 100, 250), -1)
                    )
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(300)
                }
            }
        } catch (_: Exception) {
            // Ignored if vibration not permitted or not supported
        }
    }

    fun finishWorkout() {
        val current = _activeWorkoutSession.value ?: return
        workoutTimerJob?.cancel()
        resetRestTimer()

        var totalSets = 0
        var completedSets = 0
        var totalVolumeKg = 0.0

        current.exercises.forEach { ex ->
            ex.sets.forEach { set ->
                totalSets++
                if (set.isCompleted) {
                    completedSets++
                    totalVolumeKg += (set.weightKg * set.reps)
                }
            }
        }

        val durationMinutes = maxOf(1, current.elapsedSeconds / 60)
        // Estimate calories: ~8 kcal per minute of active workout
        val calories = (durationMinutes * 8.5).toInt()

        viewModelScope.launch {
            val session = WorkoutSessionEntity(
                workoutTitle = current.workoutTitle,
                intensity = current.intensity,
                focusArea = current.focusArea,
                timestamp = System.currentTimeMillis(),
                durationSeconds = current.elapsedSeconds,
                caloriesBurned = calories,
                totalSets = totalSets,
                completedSets = completedSets,
                totalVolumeKg = totalVolumeKg,
                exerciseCount = current.exercises.size
            )
            repository.saveWorkoutSession(session)

            // Update today's activity progress
            val today = todayActivity.value
            val updatedToday = today.copy(
                caloriesBurned = today.caloriesBurned + calories,
                workoutDurationMinutes = today.workoutDurationMinutes + durationMinutes
            )
            repository.updateDailyActivity(updatedToday)
        }

        _activeWorkoutSession.value = null
    }

    fun discardWorkout() {
        workoutTimerJob?.cancel()
        resetRestTimer()
        _activeWorkoutSession.value = null
    }

    // --- Daily Activity Progress Actions ---
    fun logWater(deltaMl: Int) {
        viewModelScope.launch {
            val current = todayActivity.value
            val newWater = (current.waterIntakeMl + deltaMl).coerceAtLeast(0)
            repository.updateDailyActivity(current.copy(waterIntakeMl = newWater))
        }
    }

    fun logSteps(deltaSteps: Int) {
        viewModelScope.launch {
            val current = todayActivity.value
            val newSteps = (current.steps + deltaSteps).coerceAtLeast(0)
            // estimate additional calories for added steps: ~0.04 kcal per step
            val newCalories = current.caloriesBurned + (deltaSteps * 0.04).toInt().coerceAtLeast(0)
            repository.updateDailyActivity(current.copy(steps = newSteps, caloriesBurned = newCalories))
        }
    }

    // --- Weight Tracker Actions ---
    fun addWeightEntry(weightKg: Double, note: String) {
        viewModelScope.launch {
            val sdf = SimpleDateFormat("MMM d", Locale.US)
            val dateLabel = sdf.format(Date())
            val entry = WeightEntryEntity(
                timestamp = System.currentTimeMillis(),
                dateLabel = dateLabel,
                weightKg = weightKg,
                note = note
            )
            repository.saveWeightEntry(entry)

            // Also update current profile weight
            val currentProfile = userProfile.value
            repository.saveUserProfile(currentProfile.copy(weightKg = weightKg))
        }
    }

    fun deleteWeightEntry(id: Int) {
        viewModelScope.launch {
            repository.deleteWeightEntry(id)
        }
    }

    fun deleteWorkoutSession(id: Int) {
        viewModelScope.launch {
            repository.deleteWorkoutSession(id)
        }
    }

    // --- Profile & Unit Settings Actions ---
    fun toggleUnitPreference() {
        viewModelScope.launch {
            val current = userProfile.value
            val nextUnit = if (current.unitPrefEnum == UnitPreference.METRIC) {
                UnitPreference.IMPERIAL
            } else {
                UnitPreference.METRIC
            }
            repository.saveUserProfile(current.copy(unitPreference = nextUnit.name))
        }
    }

    fun updateProfile(
        name: String,
        age: Int,
        heightCm: Double,
        weightKg: Double,
        stepGoal: Int,
        calorieGoal: Int,
        durationGoalMinutes: Int,
        waterGoalMl: Int
    ) {
        viewModelScope.launch {
            val current = userProfile.value
            val updated = current.copy(
                name = name,
                age = age,
                heightCm = heightCm,
                weightKg = weightKg,
                stepGoal = stepGoal,
                calorieGoal = calorieGoal,
                durationGoalMinutes = durationGoalMinutes,
                waterGoalMl = waterGoalMl
            )
            repository.saveUserProfile(updated)
        }
    }
}
