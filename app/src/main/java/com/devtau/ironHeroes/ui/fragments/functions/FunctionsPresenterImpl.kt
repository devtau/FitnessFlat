package com.devtau.ironHeroes.ui.fragments.functions

import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.data.source.local.exercise.ExerciseDao
import com.devtau.ironHeroes.data.source.local.exerciseInTraining.ExerciseInTrainingDao
import com.devtau.ironHeroes.data.source.local.hero.HeroDao
import com.devtau.ironHeroes.data.source.local.muscleGroup.MuscleGroupDao
import com.devtau.ironHeroes.data.source.local.subscribeDefault
import com.devtau.ironHeroes.data.source.local.training.TrainingDao
import com.devtau.ironHeroes.enums.HumanType
import com.devtau.ironHeroes.ui.DBSubscriber
import com.devtau.ironHeroes.util.Logger
import com.devtau.ironHeroes.util.PreferencesManager
import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

class FunctionsPresenterImpl(
    private val view: FunctionsContract.View,
    private val heroDao: HeroDao,
    private val trainingDao: TrainingDao,
    private val exerciseDao: ExerciseDao,
    private val muscleGroupDao: MuscleGroupDao,
    private val exerciseInTrainingDao: ExerciseInTrainingDao,
    private val prefs: PreferencesManager
): DBSubscriber(), FunctionsContract.Presenter {

    init {
        disposeOnStop(heroDao.getListAsFlowable(HumanType.HERO.ordinal)
            .subscribeDefault(Consumer { heroes ->
                if (prefs.firstLaunch && (heroes == null || heroes.isEmpty())) {
                    createMuscleGroupsAndExercises()
                    showDemoConfigDialog()
                }
            }, "getList"))
    }


    //<editor-fold desc="Interface overrides">
    override fun restartLoaders() {

    }
    //</editor-fold>


    //<editor-fold desc="Private methods">
    private fun createMuscleGroupsAndExercises() {
        view.provideMockMuscleGroups()?.let {
            muscleGroupDao.insert(it)
                .subscribeDefault("muscleGroupDao.insert")
        }
        view.provideMockExercises()?.let {
            exerciseDao.insert(it)
                .subscribeDefault("exerciseDao.insert")
        }
    }

    private fun showDemoConfigDialog() {
        view.showMsg(R.string.load_demo_configuration, Action {
            prefs.firstLaunch = false
            loadDemoConfig()
        }, Action {
            showCreateHeroesDialog()
        })
    }

    private fun showCreateHeroesDialog() {
        view.showMsg(R.string.create_heroes, Action {
            prefs.firstLaunch = false
            view.turnPage(3)
        }, Action {
            prefs.firstLaunch = false
        })
    }

    private fun loadDemoConfig() {
        heroDao.insertListAsync(view.provideMockHeroes())
            .subscribeDefault("heroDao.insert heroes")
        heroDao.insertListAsync(view.provideMockChampions())
            .subscribeDefault("heroDao.insert champions")
        trainingDao.insertListAsync(view.provideMockTrainings())
            .subscribeDefault("trainingDao.insert")
        exerciseInTrainingDao.insert(view.provideMockExercisesInTrainings())
            .subscribeDefault("exerciseInTrainingDao.insert")
    }


    private fun sendTestToFireStore() {
        val db = FirebaseFirestore.getInstance()
        val exerciseInTraining = hashMapOf(
            "id" to 1,
            "trainingId" to 1,
            "exerciseId" to 41,
            "weight" to 0,
            "repeats" to 3,
            "count" to 20,
            "comment" to ""
        )

        db.collection("ExerciseInTraining")
            .add(exerciseInTraining)
            .addOnSuccessListener { documentReference ->
                Logger.d(LOG_TAG, "document added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Logger.w(LOG_TAG, "Error adding document $e")
            }
    }
    //</editor-fold>


    companion object {
        private const val LOG_TAG = "FunctionsPresenter"
    }
}