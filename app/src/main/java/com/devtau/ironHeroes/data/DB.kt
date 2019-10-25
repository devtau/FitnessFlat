package com.devtau.ironHeroes.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.devtau.ironHeroes.BuildConfig
import com.devtau.ironHeroes.data.dao.*
import com.devtau.ironHeroes.data.model.*

@Database(entities = [
    Champion::class,
    Hero::class,
    Training::class,
    Exercise::class,
    MuscleGroup::class
], version = SQLHelper.DB_VERSION)
abstract class DB: RoomDatabase() {

    abstract fun championDao(): ChampionDao
    abstract fun heroDao(): HeroDao
    abstract fun trainingDao(): TrainingDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun muscleGroupDao(): MuscleGroupDao


    companion object {
        @Volatile private var INSTANCE: DB? = null

        fun getInstance(context: Context): DB =
            INSTANCE ?: synchronized(this) { INSTANCE ?: buildDatabase(context).also { INSTANCE = it } }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext, DB::class.java, BuildConfig.DATABASE_NAME)
                .fallbackToDestructiveMigration().build()
    }
}