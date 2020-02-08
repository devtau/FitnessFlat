package com.devtau.ironHeroes.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.devtau.ironHeroes.BuildConfig
import com.devtau.ironHeroes.data.dao.*
import com.devtau.ironHeroes.data.model.*
import com.devtau.ironHeroes.enums.HumanType
import com.devtau.ironHeroes.util.Logger
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

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


fun <T> Flowable<T>.subscribeDefault(onNext: Consumer<T>, methodName: String): Disposable? =
    this.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(onNext, Consumer {
            Logger.e(DB.LOG_TAG, "Error in $methodName: ${it.message}")
        })

fun Completable.subscribeDefault(methodName: String) {
    var disposable: Disposable? = null
    disposable = subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
            Logger.d(DB.LOG_TAG, "Success in $methodName")
            disposable?.dispose()
        }, {
            Logger.e(DB.LOG_TAG, "Error in $methodName: ${it.message}")
            disposable?.dispose()
        })
}