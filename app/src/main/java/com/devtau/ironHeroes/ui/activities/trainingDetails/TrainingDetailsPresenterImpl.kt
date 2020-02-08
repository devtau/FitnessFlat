package com.devtau.ironHeroes.ui.activities.trainingDetails

import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.data.dao.ExerciseDao
import com.devtau.ironHeroes.data.dao.ExerciseInTrainingDao
import com.devtau.ironHeroes.data.dao.HeroDao
import com.devtau.ironHeroes.data.dao.TrainingDao
import com.devtau.ironHeroes.data.model.Exercise
import com.devtau.ironHeroes.data.model.ExerciseInTraining
import com.devtau.ironHeroes.data.model.Hero
import com.devtau.ironHeroes.data.model.Training
import com.devtau.ironHeroes.data.subscribeDefault
import com.devtau.ironHeroes.enums.HumanType
import com.devtau.ironHeroes.ui.DBSubscriber
import com.devtau.ironHeroes.util.*
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import java.util.*
import java.util.concurrent.Callable

class TrainingDetailsPresenterImpl(
    private val view: TrainingDetailsContract.View,
    private val heroDao: HeroDao,
    private val trainingDao: TrainingDao,
    private val exerciseDao: ExerciseDao,
    private val exerciseInTrainingDao: ExerciseInTrainingDao,
    private val prefs: PreferencesManager,
    private var trainingId: Long?
): DBSubscriber(), TrainingDetailsContract.Presenter {

    private var training: Training? = null
    private var champions = mutableListOf<Hero>()
    private var heroes = mutableListOf<Hero>()
    private var exercises = mutableListOf<Exercise>()
    private var exercisesInTraining = mutableListOf<ExerciseInTraining>()


    //<editor-fold desc="Interface overrides">
    override fun restartLoaders() {
        disposeOnStop(exerciseDao.getList()
            .map { relation -> relation.map { it.convert() } }
            .subscribeDefault(Consumer {
                exercises.clear()
                exercises.addAll(it)
                publishDataToView()
            }, "exerciseDao.getList"))

        disposeOnStop(heroDao.getList(HumanType.CHAMPION.ordinal)
            .subscribeDefault(Consumer {
                champions.clear()
                champions.addAll(it)
                publishDataToView()
            }, "heroDao.getList"))

        disposeOnStop(heroDao.getList(HumanType.HERO.ordinal)
            .subscribeDefault(Consumer {
                heroes.clear()
                heroes.addAll(it)
                publishDataToView()
            }, "heroDao.getList"))

        trainingId?.let {
            disposeOnStop(exerciseInTrainingDao.getListForTraining(it)
                .map { relation -> relation.map { it.convert() } }
                .subscribeDefault(Consumer { exercises ->
                    if (isOnlyOrderOfListChanged(exercisesInTraining, exercises)) return@Consumer
                    exercisesInTraining.clear()
                    exercisesInTraining.addAll(exercises)
                    publishDataToView()
                }, "exerciseInTrainingDao.getById"))

            disposeOnStop(trainingDao.getById(it)
                .map { relation -> relation.convert() }
                .subscribeDefault(Consumer { training ->
                    this.training = training
                    publishDataToView()
                }, "exerciseInTrainingDao.getById"))
        }
    }

    override fun updateTrainingData(championIndex: Int, heroIndex: Int, date: Calendar?) {
        val championId = champions[championIndex].id
        val heroId = heroes[heroIndex].id
        val trainingDate = date?.timeInMillis ?: AppUtils.getRoundDate().timeInMillis
        val allPartsPresent = Training.allObligatoryPartsPresent(championId, heroId, trainingDate)
        val someFieldsChanged = training?.someFieldsChanged(championId, heroId, trainingDate) ?: true
        Logger.d(LOG_TAG, "updateTrainingData. allPartsPresent=$allPartsPresent, someFieldsChanged=$someFieldsChanged")
        if (allPartsPresent && someFieldsChanged) {
            training = Training(trainingId, championId!!, heroId!!, trainingDate)
            Threading.async(Callable {
                trainingId = trainingDao.insert(training)
                if (training?.id == null) {
                    training?.id = trainingId
                    disposeOnStop(exerciseInTrainingDao.getListForTraining(trainingId!!)
                        .map { relation -> relation.map { it.convert() } }
                        .subscribeDefault(Consumer { exercises ->
                            exercisesInTraining.clear()
                            exercisesInTraining.addAll(exercises)
                            publishDataToView()
                        }, "exerciseInTrainingDao.getById"))
                }
            })
        }
    }

    override fun dateDialogRequested(tempDate: Calendar?) {
        val cal = tempDate?.timeInMillis ?: Calendar.getInstance().timeInMillis
        val nowMinusCentury = Calendar.getInstance()
        nowMinusCentury.add(Calendar.YEAR, -100)

        val nowPlusTwoDays = Calendar.getInstance()
        nowPlusTwoDays.add(Calendar.DAY_OF_MONTH, 2)

        val date = Calendar.getInstance()
        if (tempDate != null) date.timeInMillis = training?.date ?: cal

        view.showDateDialog(date, nowMinusCentury, nowPlusTwoDays)
    }

    override fun onBackPressed(action: Action) {
        if (training == null) {
            view.showMsg(R.string.training_not_saved, action)
        } else {
            action.run()
        }
    }

    override fun deleteTraining() {
        view.showMsg(R.string.confirm_delete, Action {
            training?.exercises?.let {
                exerciseInTrainingDao.delete(it)
                    .subscribeDefault("exerciseInTrainingDao.delete")
            }
            trainingDao.delete(listOf(training))
                .subscribeDefault("trainingDao.delete")
            view.closeScreen()
        })
    }

    override fun provideExercises(): List<ExerciseInTraining>? = training?.exercises
    override fun provideTraining() = training

    override fun onExerciseMoved(fromPosition: Int, toPosition: Int) {
        val exercises = training?.exercises as ArrayList<ExerciseInTraining>?
        if (exercises == null || exercises.isEmpty()) return
        val item = exercises.removeAt(fromPosition)
        exercises.add(toPosition, item)

        for ((i, next) in exercises.withIndex()) next.position = i
        exerciseInTrainingDao.insert(exercises)
            .subscribeDefault("exerciseInTrainingDao.insert")
    }

    override fun addExerciseClicked() = view.showNewExerciseDialog(getNextExercisePosition())
    //</editor-fold>


    //<editor-fold desc="Private methods">
    private fun publishDataToView() {
        if (exercises.isEmpty() || champions.isEmpty() || heroes.isEmpty()
            || (trainingId != null && training == null)) return

        val championId = training?.championId ?: prefs.favoriteChampionId
        val heroId = training?.heroId ?: prefs.favoriteHeroId
        val championIndex = SpinnerUtils.getSelectedHeroIndex(champions, championId)
        val heroIndex = SpinnerUtils.getSelectedHeroIndex(heroes, heroId)
        view.showChampions(SpinnerUtils.getHeroesSpinnerStrings(champions), championIndex)
        view.showHeroes(SpinnerUtils.getHeroesSpinnerStrings(heroes), heroIndex)

        if (trainingId == null) {
            view.showScreenTitle(true)
            view.showTrainingDate(AppUtils.getRoundDate())
            view.showDeleteTrainingBtn(false)
            updateTrainingData(championIndex, heroIndex, null)
        } else {
            for (nextTraining in exercisesInTraining)
                for (nextExercise in exercises)
                    if (nextTraining.exerciseId == nextExercise.id)
                        nextTraining.exercise = nextExercise
            training?.exercises = exercisesInTraining
            view.showExercises(training!!.exercises)

            view.showScreenTitle(false)
            view.showTrainingDate(training!!.getDateCal())
            view.showDeleteTrainingBtn(true)
        }

        Logger.d(LOG_TAG, "publishDataToView. " +
                "training=$training, " +
                "champions size=${champions.size}, " +
                "heroes size=${heroes.size}")
    }

    private fun isOnlyOrderOfListChanged(oldList: List<ExerciseInTraining>?, newList: List<ExerciseInTraining>?): Boolean {
        when {
            oldList == null || newList == null -> return false
            oldList.size != newList.size -> return false
            else -> {
                for (nextOld in oldList) {
                    var found = false
                    for (nextNew in newList) if (nextNew == nextOld) found = true
                    if (!found) return false
                }
                return true
            }
        }
    }

    private fun getNextExercisePosition(): Int {
        val exercises = training?.exercises
        return if (exercises == null || exercises.isEmpty()) 0
        else {
            var maxPosition = 0
            for (next in exercises) if (next.position > maxPosition) maxPosition = next.position
            maxPosition + 1
        }
    }
    //</editor-fold>


    companion object {
        private const val LOG_TAG = "TrainingDetailsPresenterImpl"
    }
}