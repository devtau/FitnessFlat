package com.devtau.ironHeroes.ui.activities.functions

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.devtau.ironHeroes.Coordinator
import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.adapters.FunctionsPagerAdapter
import com.devtau.ironHeroes.data.model.*
import com.devtau.ironHeroes.ui.DependencyRegistry
import com.devtau.ironHeroes.ui.activities.ViewSubscriberActivity
import com.devtau.ironHeroes.ui.fragments.settings.SettingsFragment
import com.devtau.ironHeroes.ui.fragments.trainingsList.TrainingsFragment
import com.devtau.ironHeroes.util.Logger
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_functions.*
import java.util.*

class FunctionsActivity: ViewSubscriberActivity(), FunctionsContract.View, SettingsFragment.Listener {

    private lateinit var presenter: FunctionsContract.Presenter
    private lateinit var coordinator: Coordinator
    private var pageIndex: Int = 0
    private var adapter: FunctionsPagerAdapter? = null


    //<editor-fold desc="Framework overrides">
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_functions)
        DependencyRegistry.inject(this)
        if (savedInstanceState != null) pageIndex = savedInstanceState.getInt(PAGE_INDEX)
        initUi()
        initPager()
        turnPage(pageIndex)
    }

    override fun onStart() {
        super.onStart()
        presenter.restartLoaders()
    }

    override fun onStop() {
        super.onStop()
        presenter.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(PAGE_INDEX, pageIndex)
        super.onSaveInstanceState(outState)
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

    override fun provideMockHeroes() = Hero.getMockHeroes(this)
    override fun provideMockChampions() = Hero.getMockChampions(this)
    override fun provideMockExercises() = Exercise.getMock(this)
    override fun provideMockMuscleGroups() = MuscleGroup.getMock(this)
    override fun provideMockTrainings() = Training.getMock(this)
    override fun provideMockExercisesInTrainings() = ExerciseInTraining.getMock(this, Locale.getDefault() == Locale("ru","RU"))

    override fun updateSpinnersVisibility() = (adapter?.getItem(0) as TrainingsFragment).updateSpinnersVisibility()
    //</editor-fold>


    fun configureWith(presenter: FunctionsContract.Presenter, coordinator: Coordinator) {
        this.presenter = presenter
        this.coordinator = coordinator
    }


    //<editor-fold desc="Private methods">
    private fun initUi() {
        trainings.setOnClickListener { turnPage(0) }
        statistics.setOnClickListener { turnPage(1) }
        settings.setOnClickListener { turnPage(2) }
        other.setOnClickListener { turnPage(3) }
    }

    private fun initPager() {
        adapter = FunctionsPagerAdapter(supportFragmentManager, coordinator)
        functionsPager?.adapter = adapter
        functionsPager?.offscreenPageLimit = 3
        functionsPager?.addOnPageChangeListener(object: ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) = turnPage(position)
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    private fun turnPage(pageIndex: Int) {
        Logger.d(LOG_TAG, "turnPage. pageIndex=$pageIndex")
        this.pageIndex = pageIndex
        applyPageIndicatorState(pageIndex)
        functionsPager?.currentItem = pageIndex
    }

    private fun applyPageIndicatorState(pageIndex: Int) {
        val context = this
        val colorActive = ContextCompat.getColor(context, R.color.colorAccent)
        val colorInactive = ContextCompat.getColor(context, R.color.secondaryTextColor)

        val page0IconActive = ContextCompat.getDrawable(context, R.drawable.ic_workouts_yellow)
        val page1IconActive = ContextCompat.getDrawable(context, R.drawable.ic_statistics_yellow)
        val page2IconActive = ContextCompat.getDrawable(context, R.drawable.ic_settings_yellow)
        val page3IconActive = ContextCompat.getDrawable(context, R.drawable.ic_other_yellow)

        val page0IconInactive = ContextCompat.getDrawable(context, R.drawable.ic_workouts_gray)
        val page1IconInactive = ContextCompat.getDrawable(context, R.drawable.ic_statistics_gray)
        val page2IconInactive = ContextCompat.getDrawable(context, R.drawable.ic_settings_gray)
        val page3IconInactive = ContextCompat.getDrawable(context, R.drawable.ic_other_gray)

        trainings?.setCompoundDrawablesWithIntrinsicBounds(null, if (pageIndex == 0) page0IconActive else page0IconInactive, null, null)
        statistics?.setCompoundDrawablesWithIntrinsicBounds(null, if (pageIndex == 1) page1IconActive else page1IconInactive, null, null)
        settings?.setCompoundDrawablesWithIntrinsicBounds(null, if (pageIndex == 2) page2IconActive else page2IconInactive, null, null)
        other?.setCompoundDrawablesWithIntrinsicBounds(null, if (pageIndex == 3) page3IconActive else page3IconInactive, null, null)

        trainings?.setTextColor(if (pageIndex == 0) colorActive else colorInactive)
        statistics?.setTextColor(if (pageIndex == 1) colorActive else colorInactive)
        settings?.setTextColor(if (pageIndex == 2) colorActive else colorInactive)
        other?.setTextColor(if (pageIndex == 3) colorActive else colorInactive)
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
        private const val PAGE_INDEX = "pageIndex"
    }
}