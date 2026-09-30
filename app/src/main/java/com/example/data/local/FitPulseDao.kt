package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.DailyActivityEntity
import com.example.data.model.UserProfileEntity
import com.example.data.model.WeightEntryEntity
import com.example.data.model.WorkoutSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FitPulseDao {
    // User Profile
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(profile: UserProfileEntity)

    // Daily Activity
    @Query("SELECT * FROM daily_activity WHERE date = :date LIMIT 1")
    fun getDailyActivity(date: String): Flow<DailyActivityEntity?>

    @Query("SELECT * FROM daily_activity ORDER BY date DESC LIMIT 30")
    fun getAllDailyActivities(): Flow<List<DailyActivityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyActivity(activity: DailyActivityEntity)

    // Weight Entries
    @Query("SELECT * FROM weight_entry ORDER BY timestamp ASC")
    fun getAllWeightEntries(): Flow<List<WeightEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeightEntry(entry: WeightEntryEntity)

    @Query("DELETE FROM weight_entry WHERE id = :id")
    suspend fun deleteWeightEntry(id: Int)

    // Workout Sessions
    @Query("SELECT * FROM workout_session ORDER BY timestamp DESC")
    fun getAllWorkoutSessions(): Flow<List<WorkoutSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutSession(session: WorkoutSessionEntity)

    @Query("DELETE FROM workout_session WHERE id = :id")
    suspend fun deleteWorkoutSession(id: Int)
}
