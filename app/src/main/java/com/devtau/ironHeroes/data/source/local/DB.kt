package com.devtau.ironHeroes.data.source.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.devtau.ironHeroes.BuildConfig
import com.devtau.ironHeroes.data.model.*
import com.devtau.ironHeroes.data.source.local.exercise.ExerciseDao
import com.devtau.ironHeroes.data.source.local.exerciseInTraining.ExerciseInTrainingDao
import com.devtau.ironHeroes.data.source.local.hero.HeroDao
import com.devtau.ironHeroes.data.source.local.muscleGroup.MuscleGroupDao
import com.devtau.ironHeroes.data.source.local.training.TrainingDao
import com.devtau.ironHeroes.enums.HumanType

@Database(entities = [
    Hero::class,
    Training::class,
    Exercise::class,
    MuscleGroup::class,
    ExerciseInTraining::class
], version = SQLHelper.DB_VERSION)
@TypeConverters(HumanType.Converter::class)

abstract class DB: RoomDatabase() {

    abstract fun heroDao(): HeroDao
    abstract fun trainingDao(): TrainingDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun muscleGroupDao(): MuscleGroupDao
    abstract fun exerciseInTrainingDao(): ExerciseInTrainingDao


    companion object {
        const val LOG_TAG = "DB_LOG"
        @Volatile private var INSTANCE: DB? = null

        fun getInstance(context: Context): DB =
            INSTANCE ?: synchronized(this) { INSTANCE ?: buildDatabase(context).also { INSTANCE = it } }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext, DB::class.java, BuildConfig.DATABASE_NAME)
                .fallbackToDestructiveMigration().build()
    }
}