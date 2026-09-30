package com.example.data.repository

import com.example.data.local.FitPulseDao
import com.example.data.model.DailyActivityEntity
import com.example.data.model.UnitConverter
import com.example.data.model.UserProfileEntity
import com.example.data.model.WeightEntryEntity
import com.example.data.model.WorkoutSessionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FitPulseRepository(private val dao: FitPulseDao) {

    val userProfile: Flow<UserProfileEntity> = dao.getUserProfile().map { profile ->
        profile ?: UserProfileEntity()
    }

    val todayActivity: Flow<DailyActivityEntity> = dao.getDailyActivity(UnitConverter.getTodayDateKey()).map { activity ->
        activity ?: DailyActivityEntity(date = UnitConverter.getTodayDateKey())
    }

    val allDailyActivities: Flow<List<DailyActivityEntity>> = dao.getAllDailyActivities()

    val weightEntries: Flow<List<WeightEntryEntity>> = dao.getAllWeightEntries()

    val workoutSessions: Flow<List<WorkoutSessionEntity>> = dao.getAllWorkoutSessions()

    suspend fun saveUserProfile(profile: UserProfileEntity) {
        dao.insertUserProfile(profile)
    }

    suspend fun updateDailyActivity(activity: DailyActivityEntity) {
        dao.insertDailyActivity(activity)
    }

    suspend fun logWater(additionalMl: Int) {
        val today = UnitConverter.getTodayDateKey()
        // Read current or default
        val current = dao.getDailyActivity(today)
        // We'll update through the view model or helper
    }

    suspend fun saveWeightEntry(entry: WeightEntryEntity) {
        dao.insertWeightEntry(entry)
    }

    suspend fun deleteWeightEntry(id: Int) {
        dao.deleteWeightEntry(id)
    }

    suspend fun saveWorkoutSession(session: WorkoutSessionEntity) {
        dao.insertWorkoutSession(session)
    }

    suspend fun deleteWorkoutSession(id: Int) {
        dao.deleteWorkoutSession(id)
    }
}
