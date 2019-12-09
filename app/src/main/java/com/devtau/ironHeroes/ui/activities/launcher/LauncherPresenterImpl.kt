package com.devtau.ironHeroes.ui.activities.launcher

import com.devtau.ironHeroes.BuildConfig
import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.data.DataLayer
import com.devtau.ironHeroes.data.model.*
import com.devtau.ironHeroes.rest.NetworkLayer
import com.devtau.ironHeroes.ui.DBSubscriber
import com.devtau.ironHeroes.util.Constants
import com.devtau.ironHeroes.util.FileUtils
import com.devtau.ironHeroes.util.PreferencesManager
import com.devtau.ironHeroes.util.Threading
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import java.util.*

class LauncherPresenterImpl(
    private val view: LauncherView,
    private val dataLayer: DataLayer,
    private val networkLayer: NetworkLayer,
    private val prefs: PreferencesManager
): DBSubscriber(), LauncherPresenter {

    private val exchangeDirName = view.resolveString(R.string.app_name)

    init {
        disposeOnStop(dataLayer.getHeroes(Consumer { heroes ->
            if (heroes == null || heroes.isEmpty()) {
                dataLayer.updateMuscleGroups(view.provideMockMuscleGroups())
                dataLayer.updateExercises(view.provideMockExercises())
                dataLayer.updateHeroes(Hero.getMockChampions())
                dataLayer.updateHeroes(Hero.getMockHeroes())

                if (BuildConfig.DEBUG) {
                    dataLayer.updateTrainings(Training.getMock())
                    dataLayer.updateExercisesInTraining(ExerciseInTraining.getMock())
                }
            }
        }))
    }


    //<editor-fold desc="Presenter overrides">
    override fun restartLoaders() {

    }

    override fun exportToFile() {
        val heroId = Hero.getMockHeroes()[0].id ?: return
        var trainingsCount = 0
        var exercisesCount = 0
        fun showExported() {
            if (trainingsCount > 0 && exercisesCount > 0) view.showExported(trainingsCount, exercisesCount)
        }

        dataLayer.getAllTrainingsAndClose(Consumer {
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
        })
        dataLayer.getAllExercisesInTrainingsAndClose(heroId, Calendar.getInstance().timeInMillis, false, Consumer {
            FileUtils.exportToJSON(it, exchangeDirName, Constants.EXERCISES_FILE_NAME, Consumer { count ->
                Threading.dispatchMain(Action {
                    if (count == null) {
                        view.showMsg(R.string.export_error)
                    } else {
                        exercisesCount = count
                        showExported()
                    }
                })
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
        FileUtils.readTrainingsJSON(exchangeDirName, Constants.TRAININGS_FILE_NAME, Consumer {
            dataLayer.updateTrainings(it)
            Threading.dispatchMain(Action {
                trainingsCount = it.size
                showReadFromFile()
            })
        })
        FileUtils.readExercisesJSON(exchangeDirName, Constants.EXERCISES_FILE_NAME, Consumer {
            dataLayer.updateExercisesInTraining(it)
            Threading.dispatchMain(Action {
                exercisesCount = it.size
                showReadFromFile()
            })
        })
    }
    //</editor-fold>


    companion object {
        private const val LOG_TAG = "LauncherPresenter"
    }
}