package com.example.data.sample

import com.example.data.model.Exercise
import com.example.data.model.FocusArea
import com.example.data.model.WorkoutIntensity
import com.example.data.model.WorkoutPlan

object PredefinedWorkouts {

    val exerciseLibrary: List<Exercise> = listOf(
        // Strength - Chest
        Exercise("ex_bench_press", "Barbell Bench Press", "Strength", "Chest", "Barbell", 4, 8, 70.0),
        Exercise("ex_incline_db_press", "Incline Dumbbell Press", "Strength", "Chest", "Dumbbell", 3, 10, 24.0),
        Exercise("ex_pushups", "Standard Push-Ups", "Strength", "Chest", "Bodyweight", 3, 15, 0.0),
        Exercise("ex_cable_fly", "Cable Chest Fly", "Strength", "Chest", "Cable", 3, 12, 14.0),

        // Strength - Back
        Exercise("ex_deadlift", "Conventional Deadlift", "Strength", "Back", "Barbell", 4, 6, 100.0),
        Exercise("ex_pullups", "Wide Grip Pull-Ups", "Strength", "Back", "Bodyweight", 3, 8, 0.0),
        Exercise("ex_barbell_row", "Bent-Over Barbell Row", "Strength", "Back", "Barbell", 3, 10, 60.0),
        Exercise("ex_lat_pulldown", "Lat Pulldown", "Strength", "Back", "Cable", 3, 12, 50.0),

        // Strength - Legs
        Exercise("ex_barbell_squat", "Barbell Back Squat", "Strength", "Legs", "Barbell", 4, 8, 85.0),
        Exercise("ex_leg_press", "45-Degree Leg Press", "Strength", "Legs", "Machine", 3, 12, 140.0),
        Exercise("ex_walking_lunges", "Dumbbell Walking Lunges", "Strength", "Legs", "Dumbbell", 3, 12, 16.0),
        Exercise("ex_romanian_deadlift", "Romanian Deadlift", "Strength", "Legs", "Barbell", 3, 10, 70.0),

        // Strength - Shoulders & Arms
        Exercise("ex_overhead_press", "Overhead Shoulder Press", "Strength", "Shoulders", "Barbell", 3, 8, 40.0),
        Exercise("ex_lateral_raise", "Dumbbell Lateral Raise", "Strength", "Shoulders", "Dumbbell", 3, 15, 8.0),
        Exercise("ex_bicep_curl", "Standing Dumbbell Curl", "Strength", "Arms", "Dumbbell", 3, 12, 12.0),
        Exercise("ex_tricep_dips", "Parallel Bar Dips", "Strength", "Arms", "Bodyweight", 3, 10, 0.0),

        // HIIT & Core
        Exercise("ex_burpees", "Explosive Burpees", "HIIT", "Full Body", "Bodyweight", 4, 15, 0.0),
        Exercise("ex_kettlebell_swing", "Kettlebell Swing", "HIIT", "Full Body", "Kettlebell", 4, 20, 20.0),
        Exercise("ex_mountain_climbers", "Mountain Climbers", "HIIT", "Core", "Bodyweight", 4, 25, 0.0),
        Exercise("ex_box_jumps", "Plyometric Box Jumps", "HIIT", "Legs", "Bodyweight", 4, 12, 0.0),
        Exercise("ex_plank", "Isometric Plank Hold", "HIIT", "Core", "Bodyweight", 3, 60, 0.0),
        Exercise("ex_hanging_leg_raise", "Hanging Leg Raise", "Strength", "Core", "Bodyweight", 3, 12, 0.0),

        // Cardio
        Exercise("ex_jump_rope", "Speed Jump Rope", "Cardio", "Full Body", "Bodyweight", 4, 100, 0.0),
        Exercise("ex_high_knees", "High Knees Sprint", "Cardio", "Full Body", "Bodyweight", 4, 30, 0.0),
        Exercise("ex_rowing_machine", "Rowing Machine Intervals", "Cardio", "Back", "Machine", 4, 250, 0.0),

        // Yoga & Mobility
        Exercise("ex_sun_salutation", "Sun Salutation Vinyasa", "Yoga", "Full Body", "Mat", 3, 5, 0.0),
        Exercise("ex_warrior_pose", "Warrior II Sequence", "Yoga", "Legs", "Mat", 3, 8, 0.0),
        Exercise("ex_downward_dog", "Downward Facing Dog Transition", "Yoga", "Full Body", "Mat", 3, 5, 0.0),
        Exercise("ex_cobra_pose", "Cobra Pose Spine Extension", "Yoga", "Back", "Mat", 3, 5, 0.0)
    )

    val sampleWorkoutPlans: List<WorkoutPlan> = listOf(
        WorkoutPlan(
            id = "plan_hiit_blast",
            title = "Metabolic HIIT Blast",
            intensity = WorkoutIntensity.INTERMEDIATE,
            focusArea = FocusArea.HIIT,
            estimatedMinutes = 30,
            estimatedCalories = 380,
            description = "High-octane interval conditioning engineered to maximize EPOC calorie afterburn and cardiovascular stamina.",
            exercises = listOf(
                exerciseLibrary.first { it.id == "ex_burpees" },
                exerciseLibrary.first { it.id == "ex_kettlebell_swing" },
                exerciseLibrary.first { it.id == "ex_mountain_climbers" },
                exerciseLibrary.first { it.id == "ex_box_jumps" }
            )
        ),
        WorkoutPlan(
            id = "plan_upper_hypertrophy",
            title = "Upper Body Hypertrophy",
            intensity = WorkoutIntensity.ADVANCED,
            focusArea = FocusArea.STRENGTH,
            estimatedMinutes = 55,
            estimatedCalories = 450,
            description = "Comprehensive chest, back, and shoulder power routine for lean mass building and upper body symmetry.",
            exercises = listOf(
                exerciseLibrary.first { it.id == "ex_bench_press" },
                exerciseLibrary.first { it.id == "ex_incline_db_press" },
                exerciseLibrary.first { it.id == "ex_barbell_row" },
                exerciseLibrary.first { it.id == "ex_overhead_press" },
                exerciseLibrary.first { it.id == "ex_bicep_curl" }
            )
        ),
        WorkoutPlan(
            id = "plan_full_body_foundation",
            title = "Full Body Foundation",
            intensity = WorkoutIntensity.BEGINNER,
            focusArea = FocusArea.STRENGTH,
            estimatedMinutes = 40,
            estimatedCalories = 310,
            description = "Master fundamental movement patterns with controlled compound exercises suitable for any fitness level.",
            exercises = listOf(
                exerciseLibrary.first { it.id == "ex_pushups" },
                exerciseLibrary.first { it.id == "ex_walking_lunges" },
                exerciseLibrary.first { it.id == "ex_lat_pulldown" },
                exerciseLibrary.first { it.id == "ex_plank" }
            )
        ),
        WorkoutPlan(
            id = "plan_cardio_endurance",
            title = "Cardio Endurance Burn",
            intensity = WorkoutIntensity.INTERMEDIATE,
            focusArea = FocusArea.CARDIO,
            estimatedMinutes = 45,
            estimatedCalories = 500,
            description = "Rhythmic aerobic conditioning and interval circuits to enhance lung capacity and torch glycogen stores.",
            exercises = listOf(
                exerciseLibrary.first { it.id == "ex_jump_rope" },
                exerciseLibrary.first { it.id == "ex_high_knees" },
                exerciseLibrary.first { it.id == "ex_rowing_machine" },
                exerciseLibrary.first { it.id == "ex_burpees" }
            )
        ),
        WorkoutPlan(
            id = "plan_vinyasa_flow",
            title = "Vinyasa Flow & Mobility",
            intensity = WorkoutIntensity.BEGINNER,
            focusArea = FocusArea.YOGA,
            estimatedMinutes = 35,
            estimatedCalories = 180,
            description = "Synchronized breathwork, deep spinal decompression, and dynamic balance sequences to unlock athletic recovery.",
            exercises = listOf(
                exerciseLibrary.first { it.id == "ex_sun_salutation" },
                exerciseLibrary.first { it.id == "ex_warrior_pose" },
                exerciseLibrary.first { it.id == "ex_downward_dog" },
                exerciseLibrary.first { it.id == "ex_cobra_pose" }
            )
        ),
        WorkoutPlan(
            id = "plan_leg_day_mastery",
            title = "Heavy Leg Day Mastery",
            intensity = WorkoutIntensity.ADVANCED,
            focusArea = FocusArea.STRENGTH,
            estimatedMinutes = 60,
            estimatedCalories = 560,
            description = "High-load lower body stimulus focusing on quad drive, posterior chain tension, and hip drive stability.",
            exercises = listOf(
                exerciseLibrary.first { it.id == "ex_barbell_squat" },
                exerciseLibrary.first { it.id == "ex_deadlift" },
                exerciseLibrary.first { it.id == "ex_leg_press" },
                exerciseLibrary.first { it.id == "ex_romanian_deadlift" }
            )
        ),
        WorkoutPlan(
            id = "plan_core_crusher",
            title = "Core Crusher & Abs",
            intensity = WorkoutIntensity.BEGINNER,
            focusArea = FocusArea.HIIT,
            estimatedMinutes = 20,
            estimatedCalories = 220,
            description = "Targeted anterior and rotational core engagement to build a bulletproof abdominal wall.",
            exercises = listOf(
                exerciseLibrary.first { it.id == "ex_plank" },
                exerciseLibrary.first { it.id == "ex_hanging_leg_raise" },
                exerciseLibrary.first { it.id == "ex_mountain_climbers" }
            )
        ),
        WorkoutPlan(
            id = "plan_power_yoga",
            title = "Power Yoga for Recovery",
            intensity = WorkoutIntensity.INTERMEDIATE,
            focusArea = FocusArea.YOGA,
            estimatedMinutes = 40,
            estimatedCalories = 210,
            description = "Active restorative poses designed for sore muscle repair, tendon elasticity, and mental focus.",
            exercises = listOf(
                exerciseLibrary.first { it.id == "ex_sun_salutation" },
                exerciseLibrary.first { it.id == "ex_downward_dog" },
                exerciseLibrary.first { it.id == "ex_warrior_pose" }
            )
        )
    )
}
