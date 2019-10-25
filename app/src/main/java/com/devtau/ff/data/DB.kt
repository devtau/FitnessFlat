package com.devtau.ff.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.devtau.ff.BuildConfig
import com.devtau.ff.data.dao.ClientDao
import com.devtau.ff.data.dao.TrainerDao
import com.devtau.ff.data.dao.TrainingDao
import com.devtau.ff.data.model.Client
import com.devtau.ff.data.model.Trainer
import com.devtau.ff.data.model.Training

@Database(entities = [
    Trainer::class,
    Client::class,
    Training::class
], version = SQLHelper.DB_VERSION)
abstract class DB: RoomDatabase() {

    abstract fun trainerDao(): TrainerDao
    abstract fun clientDao(): ClientDao
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