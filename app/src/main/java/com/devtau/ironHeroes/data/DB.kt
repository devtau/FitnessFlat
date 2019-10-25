package com.devtau.ironHeroes.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.devtau.ironHeroes.BuildConfig
import com.devtau.ironHeroes.data.dao.HeroDao
import com.devtau.ironHeroes.data.dao.ChampionDao
import com.devtau.ironHeroes.data.dao.TrainingDao
import com.devtau.ironHeroes.data.model.Hero
import com.devtau.ironHeroes.data.model.Champion
import com.devtau.ironHeroes.data.model.Training

@Database(entities = [
    Champion::class,
    Hero::class,
    Training::class
], version = SQLHelper.DB_VERSION)
abstract class DB: RoomDatabase() {

    abstract fun championDao(): ChampionDao
    abstract fun heroDao(): HeroDao
    abstract fun trainingDao(): TrainingDao


    companion object {
        @Volatile private var INSTANCE: DB? = null

        fun getInstance(context: Context): DB =
            INSTANCE ?: synchronized(this) { INSTANCE ?: buildDatabase(context).also { INSTANCE = it } }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext, DB::class.java, BuildConfig.DATABASE_NAME)
                .fallbackToDestructiveMigration().build()
    }
}