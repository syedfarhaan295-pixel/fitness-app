package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class UnitPreference {
    METRIC,   // kg, cm, ml
    IMPERIAL  // lbs, in, fl oz
}

enum class WorkoutIntensity(val label: String) {
    ALL("All"),
    BEGINNER("Beginner"),
    INTERMEDIATE("Intermediate"),
    ADVANCED("Advanced")
}

enum class FocusArea(val label: String) {
    ALL("All"),
    HIIT("HIIT"),
    STRENGTH("Strength"),
    CARDIO("Cardio"),
    YOGA("Yoga")
}

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val name: String = "Alex Stone",
    val age: Int = 26,
    val heightCm: Double = 178.0,
    val weightKg: Double = 74.5,
    val stepGoal: Int = 10000,
    val calorieGoal: Int = 650,
    val durationGoalMinutes: Int = 60,
    val waterGoalMl: Int = 3000,
    val unitPreference: String = "METRIC"
) {
    val unitPrefEnum: UnitPreference
        get() = try {
            UnitPreference.valueOf(unitPreference)
        } catch (_: Exception) {
            UnitPreference.METRIC
        }

    fun displayWeight(unit: UnitPreference = unitPrefEnum): String {
        return if (unit == UnitPreference.METRIC) {
            String.format(Locale.US, "%.1f kg", weightKg)
        } else {
            String.format(Locale.US, "%.1f lbs", weightKg * 2.20462)
        }
    }

    fun displayHeight(unit: UnitPreference = unitPrefEnum): String {
        return if (unit == UnitPreference.METRIC) {
            String.format(Locale.US, "%.0f cm", heightCm)
        } else {
            val totalInches = heightCm / 2.54
            val feet = (totalInches / 12).toInt()
            val inches = (totalInches % 12).toInt()
            "$feet' $inches\""
        }
    }
}

@Entity(tableName = "daily_activity")
data class DailyActivityEntity(
    @PrimaryKey val date: String, // e.g. "2026-09-29"
    val steps: Int = 7420,
    val caloriesBurned: Int = 485,
    val workoutDurationMinutes: Int = 42,
    val waterIntakeMl: Int = 2250
)

@Entity(tableName = "weight_entry")
data class WeightEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val dateLabel: String,
    val weightKg: Double,
    val note: String = ""
)

@Entity(tableName = "workout_session")
data class WorkoutSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val workoutTitle: String,
    val intensity: String,
    val focusArea: String,
    val timestamp: Long = System.currentTimeMillis(),
    val durationSeconds: Int,
    val caloriesBurned: Int,
    val totalSets: Int,
    val completedSets: Int,
    val totalVolumeKg: Double,
    val exerciseCount: Int
)

data class Exercise(
    val id: String,
    val name: String,
    val category: String, // HIIT, Strength, Cardio, Yoga
    val muscleGroup: String, // Chest, Back, Legs, Shoulders, Arms, Core, Full Body
    val equipment: String, // Barbell, Dumbbell, Bodyweight, Cable, Mat
    val defaultSets: Int = 3,
    val defaultReps: Int = 10,
    val defaultWeightKg: Double = 20.0
)

data class WorkoutPlan(
    val id: String,
    val title: String,
    val intensity: WorkoutIntensity,
    val focusArea: FocusArea,
    val estimatedMinutes: Int,
    val estimatedCalories: Int,
    val description: String,
    val exercises: List<Exercise>
)

data class WorkoutSet(
    val setNumber: Int,
    val previous: String = "—",
    val weightKg: Double = 0.0,
    val reps: Int = 10,
    val isCompleted: Boolean = false
)

data class ActiveExerciseLog(
    val exerciseId: String,
    val exerciseName: String,
    val muscleGroup: String,
    val sets: List<WorkoutSet>
)

data class ActiveWorkoutSession(
    val workoutTitle: String = "Quick Workout",
    val focusArea: String = "Strength",
    val intensity: String = "Intermediate",
    val startTime: Long = System.currentTimeMillis(),
    val elapsedSeconds: Int = 0,
    val isPaused: Boolean = false,
    val exercises: List<ActiveExerciseLog> = emptyList()
)

object UnitConverter {
    fun kgToLbs(kg: Double): Double = kg * 2.20462
    fun lbsToKg(lbs: Double): Double = lbs / 2.20462
    fun cmToInches(cm: Double): Double = cm / 2.54
    fun inchesToCm(inches: Double): Double = inches * 2.54
    fun mlToFlOz(ml: Int): Int = (ml * 0.033814).toInt()
    fun flOzToMl(flOz: Int): Int = (flOz / 0.033814).toInt()

    fun formatWeight(weightKg: Double, unit: UnitPreference): String {
        return if (unit == UnitPreference.METRIC) {
            String.format(Locale.US, "%.1f kg", weightKg)
        } else {
            String.format(Locale.US, "%.1f lbs", kgToLbs(weightKg))
        }
    }

    fun formatWeightValueOnly(weightKg: Double, unit: UnitPreference): String {
        return if (unit == UnitPreference.METRIC) {
            String.format(Locale.US, "%.1f", weightKg)
        } else {
            String.format(Locale.US, "%.1f", kgToLbs(weightKg))
        }
    }

    fun weightUnitLabel(unit: UnitPreference): String = if (unit == UnitPreference.METRIC) "kg" else "lbs"
    fun heightUnitLabel(unit: UnitPreference): String = if (unit == UnitPreference.METRIC) "cm" else "in"
    fun waterUnitLabel(unit: UnitPreference): String = if (unit == UnitPreference.METRIC) "ml" else "fl oz"

    fun formatWater(ml: Int, unit: UnitPreference): String {
        return if (unit == UnitPreference.METRIC) {
            "$ml ml"
        } else {
            "${mlToFlOz(ml)} fl oz"
        }
    }

    fun getTodayDateKey(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return sdf.format(Date())
    }
}
