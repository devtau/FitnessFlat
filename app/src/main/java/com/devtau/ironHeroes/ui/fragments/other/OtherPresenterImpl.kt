package com.devtau.ironHeroes.ui.fragments.other

import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.data.model.ExerciseInTraining
import com.devtau.ironHeroes.data.model.Training
import com.devtau.ironHeroes.data.source.local.exerciseInTraining.ExerciseInTrainingDao
import com.devtau.ironHeroes.data.source.local.hero.HeroDao
import com.devtau.ironHeroes.data.source.local.subscribeDefault
import com.devtau.ironHeroes.data.source.local.training.TrainingDao
import com.devtau.ironHeroes.ui.DBSubscriber
import com.devtau.ironHeroes.util.FileUtils
import com.devtau.ironHeroes.util.FileUtils.EXERCISES_FILE_NAME
import com.devtau.ironHeroes.util.FileUtils.TRAININGS_FILE_NAME
import com.devtau.ironHeroes.util.Logger
import com.devtau.ironHeroes.util.Threading
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

class OtherPresenterImpl(
    private val view: OtherContract.View,
    private val heroDao: HeroDao,
    private val trainingDao: TrainingDao,
    private val exerciseInTrainingDao: ExerciseInTrainingDao
): DBSubscriber(), OtherContract.Presenter {

    private val exchangeDirName = view.resolveString(R.string.app_name)
    private val trainings = mutableListOf<Training>()
    private val exercises = mutableListOf<ExerciseInTraining>()


    //<editor-fold desc="Interface overrides">
    override fun restartLoaders() {
        disposeOnStop(trainingDao.getListAsFlowable()
            .map { relation -> relation.map { it.convert() } }
            .subscribeDefault(Consumer {
                Logger.d(LOG_TAG, "got new trainings list with size=${it.size}")
                trainings.clear()
                trainings.addAll(it)
            }, "trainingDao.getList"))

        disposeOnStop(exerciseInTrainingDao.getList()
            .map { relation -> relation.map { it.convert() } }
            .subscribeDefault(Consumer {
                Logger.d(LOG_TAG, "got new exercisesInTrainings list with size=${it.size}")
                exercises.clear()
                exercises.addAll(it)
            }, "exerciseInTrainingDao.getListForHeroAsc"))
    }

    override fun exportToFile() {
        var trainingsExportedCount = 0
        var exercisesExportedCount = 0
        fun showExported() {
            if (trainingsExportedCount > 0 && exercisesExportedCount > 0) {
                view.showExported(trainingsExportedCount, exercisesExportedCount)
            }
        }

        if (trainings.isEmpty() || exercises.isEmpty()) {
            view.showMsg(R.string.no_exercises_or_trainings_found)
            return
        }
        FileUtils.exportToJSON(trainings, exchangeDirName, TRAININGS_FILE_NAME, Consumer {
            Threading.dispatchMain(Action {
                if (it == null) {
                    view.showMsg(R.string.export_error)
                } else {
                    trainingsExportedCount = it
                    showExported()
                }
            })
        })

        FileUtils.exportToJSON(exercises, exchangeDirName, EXERCISES_FILE_NAME, Consumer {
            Threading.dispatchMain(Action {
                if (it == null) {
                    view.showMsg(R.string.export_error)
                } else {
                    exercisesExportedCount = it
                    showExported()
                }
            })
        })
    }

    override fun importFromFile() {
        var trainingsCount = 0
        var exercisesCount = 0
        fun showReadFromFile() {
            if (trainingsCount > 0 && exercisesCount > 0) view.showReadFromFile(trainingsCount, exercisesCount)
        }
        val exchangeDirName = view.resolveString(R.string.app_name)
        FileUtils.readTrainingsJSON(exchangeDirName, TRAININGS_FILE_NAME, Consumer {
            trainingDao.insertListAsync(it)
                .subscribeDefault("trainingDao.insert")
            Threading.dispatchMain(Action {
                trainingsCount = it.size
                showReadFromFile()
            })
        })
        FileUtils.readExercisesJSON(exchangeDirName, EXERCISES_FILE_NAME, Consumer {
            exerciseInTrainingDao.insert(it)
                .subscribeDefault("exerciseInTrainingDao.insert")
            Threading.dispatchMain(Action {
                exercisesCount = it.size
                showReadFromFile()
            })
        })
    }

    override fun clearDB() {
        heroDao.deleteAsync()
            .subscribeDefault("heroDao.delete")
        trainingDao.deleteAsync()
            .subscribeDefault("trainingDao.delete")
        exerciseInTrainingDao.delete()
            .subscribeDefault("exerciseInTrainingDao.delete")
    }
    //</editor-fold>

    //<editor-fold desc="Private methods">

    //</editor-fold>


    companion object {
        private const val LOG_TAG = "OtherPresenter"
    }
}