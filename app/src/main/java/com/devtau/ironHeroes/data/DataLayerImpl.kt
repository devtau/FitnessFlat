package com.devtau.ironHeroes.data

import android.content.Context
import com.devtau.ironHeroes.data.relations.TrainingRelation
import com.devtau.ironHeroes.data.model.Hero
import com.devtau.ironHeroes.data.model.Champion
import com.devtau.ironHeroes.data.model.Training
import com.devtau.ironHeroes.util.Logger
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

class DataLayerImpl(context: Context): DataLayer {

    private var db = DB.getInstance(context)


    override fun updateChampions(list: List<Champion?>?) = if (list == null) {
        Logger.e(LOG_TAG, "updateChampions. list is null. aborting")
    } else {
        Logger.d(LOG_TAG, "updateChampions. list=$list")
        db.championDao().insert(list).subscribeDefault("updateChampions. inserted")
    }

    override fun deleteChampions(list: List<Champion?>?) = if (list == null) {
        Logger.e(LOG_TAG, "deleteChampions. list is null. aborting")
    } else {
        Logger.d(LOG_TAG, "deleteChampions. list=$list")
        db.championDao().delete(list).subscribeDefault("deleteChampions. deleted")
    }

    override fun updateHeroes(list: List<Hero?>?) = if (list == null) {
        Logger.e(LOG_TAG, "updateHeroes. list is null. aborting")
    } else {
        Logger.d(LOG_TAG, "updateHeroes. list=$list")
        db.heroDao().insert(list).subscribeDefault("updateHeroes. inserted")
    }

    override fun deleteHeroes(list: List<Hero?>?) = if (list == null) {
        Logger.e(LOG_TAG, "deleteHeroes. list is null. aborting")
    } else {
        Logger.d(LOG_TAG, "deleteHeroes. list=$list")
        db.heroDao().delete(list).subscribeDefault("deleteHeroes. deleted")
    }

    override fun updateTrainings(list: List<Training?>?) = if (list == null) {
        Logger.e(LOG_TAG, "updateTrainings. list is null. aborting")
    } else {
        Logger.d(LOG_TAG, "updateTrainings. list=$list")
        db.trainingDao().insert(list).subscribeDefault("updateTrainings. inserted")
    }

    override fun deleteTrainings(list: List<Training?>?) = if (list == null) {
        Logger.e(LOG_TAG, "deleteTrainings. list is null. aborting")
    } else {
        Logger.d(LOG_TAG, "deleteTrainings. list=$list")
        db.trainingDao().delete(list).subscribeDefault("deleteTrainings. deleted")
    }

    override fun clearDB() {
        Logger.w(LOG_TAG, "going to clearDB")
        db.heroDao().delete().subscribeDefault("clearDB. orders deleted")
    }

    override fun getHero(id: Long, listener: Consumer<Hero?>): Disposable = db.heroDao().getById(id)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({ listener.accept(if (it.isEmpty()) null else it) })
        { Logger.e(LOG_TAG, "Error in getHero: ${it.message}") }

    override fun getHeroes(listener: Consumer<List<Hero>?>): Disposable = db.heroDao().getList()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({ listener.accept(it) })
        { Logger.e(LOG_TAG, "Error in getHeroes: ${it.message}") }

    override fun getTraining(id: Long, listener: Consumer<Training?>): Disposable = db.trainingDao().getById(id)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({ listener.accept(if (it.isEmpty()) null else it) })
        { Logger.e(LOG_TAG, "Error in getTraining: ${it.message}") }

    override fun getTrainings(listener: Consumer<List<Training>?>): Disposable = db.trainingDao().getList()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .map { TrainingRelation.convertToTrainings(it) }
        .subscribe({ listener.accept(it) })
        { Logger.e(LOG_TAG, "Error in getTrainings: ${it.message}") }

    override fun getHeroByIdAndClose(id: Long, listener: Consumer<Hero?>) {
        var disposable: Disposable? = null
        disposable = db.heroDao().getById(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                listener.accept(if (it.isEmpty()) null else it)
                disposable?.dispose()
            }) { Logger.e(LOG_TAG, "Error in getHeroByIdAndClose: ${it.message}") }
    }

    override fun getTrainingByIdAndClose(id: Long, listener: Consumer<Training?>) {
        var disposable: Disposable? = null
        disposable = db.trainingDao().getById(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                listener.accept(if (it.isEmpty()) null else it)
                disposable?.dispose()
            }) { Logger.e(LOG_TAG, "Error in getTrainingByIdAndClose: ${it.message}") }
    }


    private fun Completable.subscribeDefault(msg: String?) {
        var disposable: Disposable? = null
        disposable = subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe {
            Logger.d(LOG_TAG, msg)
            disposable?.dispose()
        }
    }


    companion object {
        private const val LOG_TAG = "DataLayer"
    }
}