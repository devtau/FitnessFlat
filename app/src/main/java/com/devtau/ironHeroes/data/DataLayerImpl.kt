package com.devtau.ironHeroes.data

import android.content.Context
import com.devtau.ironHeroes.data.model.*
import com.devtau.ironHeroes.data.relations.ExerciseInTrainingRelation
import com.devtau.ironHeroes.data.relations.ExerciseRelation
import com.devtau.ironHeroes.data.relations.TrainingRelation
import com.devtau.ironHeroes.enums.HumanType
import com.devtau.ironHeroes.util.Constants.EMPTY_OBJECT_ID
import com.devtau.ironHeroes.util.Logger
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import java.util.*

class DataLayerImpl(context: Context): DataLayer {

    private var db = DB.getInstance(context)


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

    override fun updateTraining(training: Training?) = if (training == null) {
        Logger.e(LOG_TAG, "updateTrainings. training is null. aborting")
        EMPTY_OBJECT_ID
    } else {
        Logger.d(LOG_TAG, "updateTrainings. training=$training")
        db.trainingDao().insert(training)
    }

    override fun deleteTrainings(list: List<Training?>?) = if (list == null) {
        Logger.e(LOG_TAG, "deleteTrainings. list is null. aborting")
    } else {
        Logger.d(LOG_TAG, "deleteTrainings. list=$list")
        db.trainingDao().delete(list).subscribeDefault("deleteTrainings. deleted")
    }

    override fun updateExercises(list: List<Exercise?>?) = if (list == null) {
        Logger.e(LOG_TAG, "updateExercises. list is null. aborting")
    } else {
        Logger.d(LOG_TAG, "updateExercises. list=$list")
        db.exerciseDao().insert(list).subscribeDefault("updateExercises. inserted")
    }

    override fun deleteExercises(list: List<Exercise?>?) = if (list == null) {
        Logger.e(LOG_TAG, "deleteExercises. list is null. aborting")
    } else {
        Logger.d(LOG_TAG, "deleteExercises. list=$list")
        db.exerciseDao().delete(list).subscribeDefault("deleteExercises. deleted")
    }

    override fun updateExercisesInTraining(list: List<ExerciseInTraining?>?) = if (list == null) {
        Logger.e(LOG_TAG, "updateExercisesInTraining. list is null. aborting")
    } else {
        Logger.d(LOG_TAG, "updateExercisesInTraining. list=$list")
        db.exerciseInTrainingDao().insert(list).subscribeDefault("updateExercisesInTraining. inserted")
    }

    override fun deleteExercisesInTraining(list: List<ExerciseInTraining?>?) = if (list == null) {
        Logger.e(LOG_TAG, "deleteExercisesInTraining. list is null. aborting")
    } else {
        Logger.d(LOG_TAG, "deleteExercisesInTraining. list=$list")
        db.exerciseInTrainingDao().delete(list).subscribeDefault("deleteExercisesInTraining. deleted")
    }

    override fun updateMuscleGroups(list: List<MuscleGroup?>?) = if (list == null) {
        Logger.e(LOG_TAG, "updateMuscleGroups. list is null. aborting")
    } else {
        Logger.d(LOG_TAG, "updateMuscleGroups. list=$list")
        db.muscleGroupDao().insert(list).subscribeDefault("updateMuscleGroups. inserted")
    }

    override fun deleteMuscleGroups(list: List<MuscleGroup?>?) = if (list == null) {
        Logger.e(LOG_TAG, "deleteMuscleGroups. list is null. aborting")
    } else {
        Logger.d(LOG_TAG, "deleteMuscleGroups. list=$list")
        db.muscleGroupDao().delete(list).subscribeDefault("deleteMuscleGroups. deleted")
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

    override fun getHeroes(listener: Consumer<List<Hero>?>): Disposable = db.heroDao()
        .getList(HumanType.HERO.ordinal)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({ listener.accept(it) })
        { Logger.e(LOG_TAG, "Error in getHeroes: ${it.message}") }

    override fun getChampions(listener: Consumer<List<Hero>?>): Disposable = db.heroDao()
        .getList(HumanType.CHAMPION.ordinal)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({ listener.accept(it) })
        { Logger.e(LOG_TAG, "Error in getHeroes: ${it.message}") }

    override fun getTraining(id: Long, listener: Consumer<Training?>): Disposable = db.trainingDao().getById(id)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .map(TrainingRelation::convert)
        .subscribe({ listener.accept(if (it.isEmpty()) null else it) })
        { Logger.e(LOG_TAG, "Error in getTraining: ${it.message}") }

    override fun getTrainings(listener: Consumer<List<Training>>): Disposable = db.trainingDao().getList()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .map { TrainingRelation.convertList(it) }
        .subscribe({ listener.accept(it) })
        { Logger.e(LOG_TAG, "Error in getTrainings: ${it.message}") }

    override fun getExercise(id: Long, listener: Consumer<Exercise?>): Disposable = db.exerciseDao().getById(id)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .map(ExerciseRelation::convert)
        .subscribe({ listener.accept(if (it.isEmpty()) null else it) })
        { Logger.e(LOG_TAG, "Error in getExercise: ${it.message}") }

    override fun getExercises(listener: Consumer<List<Exercise>?>): Disposable = db.exerciseDao().getList()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .map { ExerciseRelation.convertList(it) }
        .subscribe({ listener.accept(it) })
        { Logger.e(LOG_TAG, "Error in getExercises: ${it.message}") }

    override fun getExerciseInTraining(id: Long, listener: Consumer<ExerciseInTraining?>): Disposable
            = db.exerciseInTrainingDao().getById(id)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .map(ExerciseInTrainingRelation::convert)
        .subscribe({ listener.accept(if (it.isEmpty()) null else it) })
        { Logger.e(LOG_TAG, "Error in getExercise: ${it.message}") }

    override fun getExercisesInTraining(trainingId: Long, listener: Consumer<List<ExerciseInTraining>?>): Disposable
            = db.exerciseInTrainingDao().getList(trainingId)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .map { ExerciseInTrainingRelation.convertList(it) }
        .subscribe({ listener.accept(it) })
        { Logger.e(LOG_TAG, "Error in getExercises: ${it.message}") }

    override fun getMuscleGroup(id: Long, listener: Consumer<MuscleGroup?>): Disposable = db.muscleGroupDao().getById(id)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({ listener.accept(if (it.isEmpty()) null else it) })
        { Logger.e(LOG_TAG, "Error in getMuscleGroup: ${it.message}") }

    override fun getMuscleGroups(listener: Consumer<List<MuscleGroup>?>): Disposable = db.muscleGroupDao().getList()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({ listener.accept(it) })
        { Logger.e(LOG_TAG, "Error in getMuscleGroups: ${it.message}") }

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
            .map(TrainingRelation::convert)
            .subscribe({
                listener.accept(if (it.isEmpty()) null else it)
                disposable?.dispose()
            }) { Logger.e(LOG_TAG, "Error in getTrainingByIdAndClose: ${it.message}") }
    }

    override fun getExerciseAndClose(id: Long, listener: Consumer<Exercise?>) {
        var disposable: Disposable? = null
        disposable = db.exerciseDao().getById(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map(ExerciseRelation::convert)
            .subscribe({
                listener.accept(if (it.isEmpty()) null else it)
                disposable?.dispose()
            }) { Logger.e(LOG_TAG, "Error in getExerciseAndClose: ${it.message}") }
    }

    override fun getExercisesAndClose(listener: Consumer<List<Exercise>?>) {
        var disposable: Disposable? = null
        disposable = db.exerciseDao().getList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { ExerciseRelation.convertList(it) }
            .subscribe({
                listener.accept(if (it.isEmpty()) null else it)
                disposable?.dispose()
            }) { Logger.e(LOG_TAG, "Error in getExercisesAndClose: ${it.message}") }
    }

    override fun getExercisesInTrainingAndClose(trainingId: Long, listener: Consumer<List<ExerciseInTraining>?>) {
        var disposable: Disposable? = null
        disposable = db.exerciseInTrainingDao().getList(trainingId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { ExerciseInTrainingRelation.convertList(it) }
            .subscribe({
                listener.accept(if (it.isEmpty()) null else it)
                disposable?.dispose()
            }) { Logger.e(LOG_TAG, "Error in getExercisesInTrainingAndClose: ${it.message}") }
    }

    override fun getExerciseInTrainingAndClose(id: Long, listener: Consumer<ExerciseInTraining?>) {
        var disposable: Disposable? = null
        disposable = db.exerciseInTrainingDao().getById(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map(ExerciseInTrainingRelation::convert)
            .subscribe({
                listener.accept(it)
                disposable?.dispose()
            }) { Logger.e(LOG_TAG, "Error in getExerciseInTrainingAndClose: ${it.message}") }
    }

    override fun getAllExercisesInTrainingsAndClose(heroId: Long, maxRelevantDate: Long, listener: Consumer<List<ExerciseInTraining>?>) {
        var disposable: Disposable? = null
        val maxDate = Calendar.getInstance()
        maxDate.add(Calendar.DAY_OF_MONTH, -1)
        disposable = db.exerciseInTrainingDao().getListForHero(heroId, maxRelevantDate)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { ExerciseInTrainingRelation.convertList(it) }
            .subscribe({
                listener.accept(it)
                disposable?.dispose()
            }) { Logger.e(LOG_TAG, "Error in getAllExercisesInTrainingsAndClose: ${it.message}") }
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