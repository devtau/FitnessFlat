package com.devtau.ironHeroes.ui.activities.functions

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.data.model.Exercise
import com.devtau.ironHeroes.data.model.Hero
import com.devtau.ironHeroes.data.model.MuscleGroup
import com.devtau.ironHeroes.ui.DependencyRegistry
import com.devtau.ironHeroes.ui.activities.ViewSubscriberActivity
import com.devtau.ironHeroes.ui.fragments.ActionsFragment
import com.devtau.ironHeroes.ui.fragments.other.OtherFragment
import com.devtau.ironHeroes.ui.fragments.settings.SettingsFragment
import com.devtau.ironHeroes.ui.fragments.statistics.StatisticsFragment
import com.devtau.ironHeroes.ui.fragments.trainingsList.TrainingsFragment
import com.devtau.ironHeroes.util.Logger
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_functions.*

class FunctionsActivity: ViewSubscriberActivity(), FunctionsView, ActionsFragment.Listener {

    lateinit var presenter: FunctionsPresenter
    private var pageIndex: Int = 0


    //<editor-fold desc="Framework overrides">
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_functions)
        DependencyRegistry().inject(this)
        initUi()
        turnPage(0)
    }

    override fun onStart() {
        super.onStart()
        presenter.restartLoaders()
    }

    override fun onStop() {
        super.onStop()
        presenter.onStop()
    }
    //</editor-fold>


    //<editor-fold desc="View overrides">
    override fun getLogTag() = LOG_TAG

    override fun showExported(trainingsCount: Int, exercisesCount: Int) {
        val trainings = resources.getQuantityString(R.plurals.trainings, trainingsCount, trainingsCount)
        val exercises = resources.getQuantityString(R.plurals.exercises, exercisesCount, exercisesCount)
        showMsg(String.format(getString(R.string.exported_formatter), trainings, exercises))
    }

    override fun showReadFromFile(trainingsCount: Int, exercisesCount: Int) {
        val trainings = resources.getQuantityString(R.plurals.trainings, trainingsCount, trainingsCount)
        val exercises = resources.getQuantityString(R.plurals.exercises, exercisesCount, exercisesCount)
        showMsg(String.format(getString(R.string.imported_formatter), trainings, exercises))
    }

    override fun provideMockExercises() = Exercise.getMock(this)
    override fun provideMockMuscleGroups() = MuscleGroup.getMock(this)
    //</editor-fold>


    //<editor-fold desc="Private methods">
    private fun initUi() {
        trainings.setOnClickListener { turnPage(0) }
        statistics.setOnClickListener { turnPage(1) }
        settings.setOnClickListener { turnPage(2) }
        other.setOnClickListener { turnPage(3) }
    }

    private fun turnPage(pageIndex: Int) {
        this.pageIndex = pageIndex

        val fragmentTag = when (pageIndex) {
            0 -> TrainingsFragment.FRAGMENT_TAG
            1 -> StatisticsFragment.FRAGMENT_TAG
            2 -> SettingsFragment.FRAGMENT_TAG
            3 -> OtherFragment.FRAGMENT_TAG
            else -> null
        } ?: return

        try {
            if (supportFragmentManager.findFragmentByTag(fragmentTag) != null) return
        } catch (e: java.lang.IllegalStateException) {
            return
        }

        val fragment = when (pageIndex) {
            0 -> TrainingsFragment.newInstance()
            1 -> StatisticsFragment.newInstance(Hero.getMockHeroes()[0].id!!)
            2 -> SettingsFragment.newInstance()
            3 -> OtherFragment.newInstance()
            else -> null
        } ?: return

        supportFragmentManager.beginTransaction().replace(R.id.functionsFrame, fragment, fragmentTag).commit()
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
        private const val LOG_TAG = "FunctionsActivity"

        fun newInstance(context: Context) {
            val intent = Intent(context, FunctionsActivity::class.java)
            context.startActivity(intent)
        }
    }
}