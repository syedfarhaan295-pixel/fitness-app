package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.DailyActivityEntity
import com.example.data.model.UnitConverter
import com.example.data.model.UserProfileEntity
import com.example.data.model.WeightEntryEntity
import com.example.data.model.WorkoutSessionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

@Database(
    entities = [
        UserProfileEntity::class,
        DailyActivityEntity::class,
        WeightEntryEntity::class,
        WorkoutSessionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class FitPulseDatabase : RoomDatabase() {
    abstract fun fitPulseDao(): FitPulseDao

    companion object {
        @Volatile
        private var INSTANCE: FitPulseDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): FitPulseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FitPulseDatabase::class.java,
                    "fitpulse_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.fitPulseDao())
                    }
                }
            }

            private suspend fun populateInitialData(dao: FitPulseDao) {
                // 1. Initial User Profile
                dao.insertUserProfile(
                    UserProfileEntity(
                        id = 1,
                        name = "Alex Stone",
                        age = 26,
                        heightCm = 178.0,
                        weightKg = 74.5,
                        stepGoal = 10000,
                        calorieGoal = 650,
                        durationGoalMinutes = 60,
                        waterGoalMl = 3000,
                        unitPreference = "METRIC"
                    )
                )

                // 2. Today's initial activity
                val today = UnitConverter.getTodayDateKey()
                dao.insertDailyActivity(
                    DailyActivityEntity(
                        date = today,
                        steps = 7420,
                        caloriesBurned = 485,
                        workoutDurationMinutes = 42,
                        waterIntakeMl = 2250
                    )
                )

                // 3. Past 7 days of realistic weight entries for the progress trend chart
                val cal = Calendar.getInstance()
                val weightDays = listOf(
                    Pair(-6, 75.8),
                    Pair(-5, 75.5),
                    Pair(-4, 75.3),
                    Pair(-3, 74.9),
                    Pair(-2, 74.8),
                    Pair(-1, 74.6),
                    Pair(0, 74.5)
                )
                val dayLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Today")
                weightDays.forEachIndexed { index, (dayOffset, weight) ->
                    val entryCal = Calendar.getInstance()
                    entryCal.add(Calendar.DAY_OF_YEAR, dayOffset)
                    dao.insertWeightEntry(
                        WeightEntryEntity(
                            timestamp = entryCal.timeInMillis,
                            dateLabel = dayLabels.getOrElse(index) { "Day $index" },
                            weightKg = weight,
                            note = if (index == 6) "Morning weigh-in, feeling energized" else ""
                        )
                    )
                }

                // 4. Past completed workout sessions
                val pastSessions = listOf(
                    WorkoutSessionEntity(
                        workoutTitle = "Metabolic HIIT Blast",
                        intensity = "Intermediate",
                        focusArea = "HIIT",
                        timestamp = System.currentTimeMillis() - 86400000L * 1, // Yesterday
                        durationSeconds = 1800, // 30 mins
                        caloriesBurned = 380,
                        totalSets = 12,
                        completedSets = 12,
                        totalVolumeKg = 1450.0,
                        exerciseCount = 4
                    ),
                    WorkoutSessionEntity(
                        workoutTitle = "Upper Body Hypertrophy",
                        intensity = "Advanced",
                        focusArea = "Strength",
                        timestamp = System.currentTimeMillis() - 86400000L * 2, // 2 days ago
                        durationSeconds = 3300, // 55 mins
                        caloriesBurned = 430,
                        totalSets = 16,
                        completedSets = 16,
                        totalVolumeKg = 4200.0,
                        exerciseCount = 5
                    ),
                    WorkoutSessionEntity(
                        workoutTitle = "Full Body Foundation",
                        intensity = "Beginner",
                        focusArea = "Strength",
                        timestamp = System.currentTimeMillis() - 86400000L * 4,
                        durationSeconds = 2400,
                        caloriesBurned = 310,
                        totalSets = 12,
                        completedSets = 12,
                        totalVolumeKg = 2100.0,
                        exerciseCount = 4
                    ),
                    WorkoutSessionEntity(
                        workoutTitle = "Cardio Endurance Burn",
                        intensity = "Intermediate",
                        focusArea = "Cardio",
                        timestamp = System.currentTimeMillis() - 86400000L * 5,
                        durationSeconds = 2700,
                        caloriesBurned = 490,
                        totalSets = 8,
                        completedSets = 8,
                        totalVolumeKg = 600.0,
                        exerciseCount = 3
                    )
                )
                pastSessions.forEach { dao.insertWorkoutSession(it) }
            }
        }
    }
}
