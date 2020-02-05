package com.devtau.ironHeroes.ui.fragments.other

import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.data.dao.*
import com.devtau.ironHeroes.data.relations.ExerciseInTrainingRelation
import com.devtau.ironHeroes.data.relations.TrainingRelation
import com.devtau.ironHeroes.data.subscribeDefault
import com.devtau.ironHeroes.ui.DBSubscriber
import com.devtau.ironHeroes.util.Constants
import com.devtau.ironHeroes.util.FileUtils
import com.devtau.ironHeroes.util.Threading
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

class OtherPresenterImpl(
    private val view: OtherContract.View,
    private val heroDao: HeroDao,
    private val trainingDao: TrainingDao,
    private val exerciseInTrainingDao: ExerciseInTrainingDao
): DBSubscriber(), OtherContract.Presenter {

    private val exchangeDirName = view.resolveString(R.string.app_name)

    //<editor-fold desc="Presenter overrides">
    override fun restartLoaders() {

    }

    override fun exportToFile() {
        val heroId = view.provideMockHeroes()[0].id ?: return
        var trainingsCount = 0
        var exercisesCount = 0
        fun showExported() {
            if (trainingsCount > 0 && exercisesCount > 0) view.showExported(trainingsCount, exercisesCount)
        }

        var trainingsDisposable: Disposable? = null
        trainingsDisposable = trainingDao.getList()
            .map { TrainingRelation.convertList(it) }
            .subscribeDefault(Consumer {
                FileUtils.exportToJSON(it, exchangeDirName, Constants.TRAININGS_FILE_NAME, Consumer { count ->
                    Threading.dispatchMain(Action {
                        if (count == null) {
                            view.showMsg(R.string.export_error)
                        } else {
                            trainingsCount = count
                            showExported()
                        }
                    })
                })
                trainingsDisposable?.dispose()
            }, "trainingDao.getList")

        var exercisesDisposable: Disposable? = null
        exercisesDisposable = exerciseInTrainingDao.getListForHeroAsc(heroId)
            .map { relation -> ExerciseInTrainingRelation.convertList(relation) }
            .subscribeDefault(Consumer {
                FileUtils.exportToJSON(it, exchangeDirName, Constants.EXERCISES_FILE_NAME, Consumer { count ->
                    Threading.dispatchMain(Action {
                        if (count == null) {
                            view.showMsg(R.string.export_error)
                        } else {
                            exercisesCount = count
                            showExported()
                        }
                    })
                    exercisesDisposable?.dispose()
                })
            }, "exerciseInTrainingDao.getListForHeroAsc")
    }

    override fun importFromFile() {
        var trainingsCount = 0
        var exercisesCount = 0
        fun showReadFromFile() {
            if (trainingsCount > 0 && exercisesCount > 0) view.showReadFromFile(trainingsCount, exercisesCount)
        }
        val exchangeDirName = view.resolveString(R.string.app_name)
        FileUtils.readTrainingsJSON(exchangeDirName, Constants.TRAININGS_FILE_NAME, Consumer {
            trainingDao.insert(it).subscribeDefault("updateTrainings. inserted")
            Threading.dispatchMain(Action {
                trainingsCount = it.size
                showReadFromFile()
            })
        })
        FileUtils.readExercisesJSON(exchangeDirName, Constants.EXERCISES_FILE_NAME, Consumer {
            exerciseInTrainingDao.insert(it).subscribeDefault("updateExercisesInTraining. inserted")
            Threading.dispatchMain(Action {
                exercisesCount = it.size
                showReadFromFile()
            })
        })
    }

    override fun clearDB() {
        heroDao.delete().subscribeDefault("clearDB. heroes & champions deleted")
        trainingDao.delete().subscribeDefault("clearDB. trainings deleted")
        exerciseInTrainingDao.delete().subscribeDefault("clearDB. exercises in trainings deleted")
    }
    //</editor-fold>

    //<editor-fold desc="Private methods">

    //</editor-fold>


    companion object {
        private const val LOG_TAG = "OtherPresenter"
    }
}