package com.devtau.ff.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.devtau.ff.BuildConfig
import com.devtau.ff.db.dao.ClientDAO
import com.devtau.ff.db.tables.ClientStored

@Database(entities = [
    ClientStored::class
], version = SQLHelper.DB_VERSION)
abstract class DB: RoomDatabase() {

    abstract fun clientDao(): ClientDAO


    companion object {
        @Volatile private var INSTANCE: DB? = null

        fun getInstance(context: Context): DB =
            INSTANCE ?: synchronized(this) { INSTANCE ?: buildDatabase(context).also { INSTANCE = it } }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext, DB::class.java, BuildConfig.DATABASE_NAME)
                .fallbackToDestructiveMigration().build()
    }
}