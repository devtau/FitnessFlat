package com.devtau.ironHeroes.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import androidx.room.TypeConverters
import com.devtau.ironHeroes.BuildConfig
import com.devtau.ironHeroes.data.dao.*
import com.devtau.ironHeroes.data.model.*
import com.devtau.ironHeroes.enums.HumanType

@Database(entities = [
    Hero::class,
    Training::class,
    Exercise::class,
    MuscleGroup::class
], version = SQLHelper.DB_VERSION)
@TypeConverters(HumanType.Converter::class)
abstract class DB: RoomDatabase() {

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