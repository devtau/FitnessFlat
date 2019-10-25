package com.devtau.ff.data

import android.content.Context
import com.devtau.ff.data.relations.TrainingRelation
import com.devtau.ff.data.model.Client
import com.devtau.ff.data.model.Trainer
import com.devtau.ff.data.model.Training
import com.devtau.ff.util.Logger
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

class DataLayerImpl(context: Context): DataLayer {

    private var db = DB.getInstance(context)


    override fun updateTrainers(list: List<Trainer?>?) = if (list == null) {
        Logger.e(LOG_TAG, "updateTrainers. list is null. aborting")
    } else {
        Logger.d(LOG_TAG, "updateTrainers. list=$list")
        db.trainerDao().insert(list).subscribeDefault("updateTrainers. inserted")
    }

    override fun deleteTrainers(list: List<Trainer?>?) = if (list == null) {
        Logger.e(LOG_TAG, "deleteTrainers. list is null. aborting")
    } else {
        Logger.d(LOG_TAG, "deleteTrainers. list=$list")
        db.trainerDao().delete(list).subscribeDefault("deleteTrainers. deleted")
    }

    override fun updateClients(list: List<Client?>?) = if (list == null) {
        Logger.e(LOG_TAG, "updateClients. list is null. aborting")
    } else {
        Logger.d(LOG_TAG, "updateClients. list=$list")
        db.clientDao().insert(list).subscribeDefault("updateClients. inserted")
    }

    override fun deleteClients(list: List<Client?>?) = if (list == null) {
        Logger.e(LOG_TAG, "deleteClients. list is null. aborting")
    } else {
        Logger.d(LOG_TAG, "deleteClients. list=$list")
        db.clientDao().delete(list).subscribeDefault("deleteClients. deleted")
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
        db.clientDao().delete().subscribeDefault("clearDB. orders deleted")
    }

    override fun getClient(id: Long, listener: Consumer<Client?>): Disposable = db.clientDao().getById(id)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({ listener.accept(if (it.isEmpty()) null else it) })
        { Logger.e(LOG_TAG, "Error in getClient: ${it.message}") }

    override fun getClients(listener: Consumer<List<Client>?>): Disposable = db.clientDao().getList()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({ listener.accept(it) })
        { Logger.e(LOG_TAG, "Error in getClients: ${it.message}") }

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

    override fun getClientByIdAndClose(id: Long, listener: Consumer<Client?>) {
        var disposable: Disposable? = null
        disposable = db.clientDao().getById(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                listener.accept(if (it.isEmpty()) null else it)
                disposable?.dispose()
            }) { Logger.e(LOG_TAG, "Error in getClientByIdAndClose: ${it.message}") }
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