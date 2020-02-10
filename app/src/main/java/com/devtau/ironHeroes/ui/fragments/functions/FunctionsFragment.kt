package com.devtau.ironHeroes.ui.fragments.functions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.adapters.FunctionsPagerAdapter
import com.devtau.ironHeroes.data.model.*
import com.devtau.ironHeroes.ui.Coordinator
import com.devtau.ironHeroes.ui.DependencyRegistry
import com.devtau.ironHeroes.ui.fragments.ViewSubscriberFragment
import com.devtau.ironHeroes.ui.fragments.settings.SettingsFragment
import com.devtau.ironHeroes.ui.fragments.trainingsList.TrainingsFragment
import com.devtau.ironHeroes.util.Logger
import kotlinx.android.synthetic.main.fragment_functions.*
import java.util.*

class FunctionsFragment: ViewSubscriberFragment(), FunctionsContract.View, SettingsFragment.Listener {

    private lateinit var presenter: FunctionsContract.Presenter
    private lateinit var coordinator: Coordinator
    private var pageIndex: Int = 0
    private var pagerAdapter: FunctionsPagerAdapter? = null


    //<editor-fold desc="Framework overrides">
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DependencyRegistry.inject(this)
        if (savedInstanceState != null) pageIndex = savedInstanceState.getInt(PAGE_INDEX)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_functions, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        initPager()
        turnPage(pageIndex)
        setHasOptionsMenu(false)
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


    //<editor-fold desc="Interface overrides">
    override fun getLogTag() = LOG_TAG
    override fun initActionbar() = true

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

    override fun provideMockHeroes() = context?.let { Hero.getMockHeroes(it) }
    override fun provideMockChampions() = context?.let { Hero.getMockChampions(it) }
    override fun provideMockExercises() = context?.let { Exercise.getMock(it) }
    override fun provideMockMuscleGroups() = context?.let { MuscleGroup.getMock(it) }
    override fun provideMockTrainings() = context?.let { Training.getMock(it) }
    override fun provideMockExercisesInTrainings() = context?.let {
        ExerciseInTraining.getMock(it, Locale.getDefault() == Locale("ru","RU"))
    }

    override fun turnPage(pageIndex: Int) {
        Logger.d(LOG_TAG, "turnPage. pageIndex=$pageIndex")
        this.pageIndex = pageIndex
        applyPageIndicatorState(pageIndex)
        functionsPager?.currentItem = pageIndex
    }

    override fun updateSpinnersVisibility() = (pagerAdapter?.getItem(0) as TrainingsFragment).updateSpinnersVisibility()
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
        pagerAdapter = FunctionsPagerAdapter(childFragmentManager, coordinator)
        functionsPager?.adapter = pagerAdapter
        functionsPager?.offscreenPageLimit = 3
        functionsPager?.addOnPageChangeListener(object: ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) = turnPage(position)
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    private fun applyPageIndicatorState(pageIndex: Int) = context?.let {
        val colorActive = ContextCompat.getColor(it, R.color.colorAccent)
        val colorInactive = ContextCompat.getColor(it, R.color.secondaryTextColor)

        val page0IconActive = ContextCompat.getDrawable(it, R.drawable.ic_workouts_yellow)
        val page1IconActive = ContextCompat.getDrawable(it, R.drawable.ic_statistics_yellow)
        val page2IconActive = ContextCompat.getDrawable(it, R.drawable.ic_settings_yellow)
        val page3IconActive = ContextCompat.getDrawable(it, R.drawable.ic_other_yellow)

        val page0IconInactive = ContextCompat.getDrawable(it, R.drawable.ic_workouts_gray)
        val page1IconInactive = ContextCompat.getDrawable(it, R.drawable.ic_statistics_gray)
        val page2IconInactive = ContextCompat.getDrawable(it, R.drawable.ic_settings_gray)
        val page3IconInactive = ContextCompat.getDrawable(it, R.drawable.ic_other_gray)

        trainings?.setCompoundDrawablesWithIntrinsicBounds(null, if (pageIndex == 0) page0IconActive else page0IconInactive, null, null)
        statistics?.setCompoundDrawablesWithIntrinsicBounds(null, if (pageIndex == 1) page1IconActive else page1IconInactive, null, null)
        settings?.setCompoundDrawablesWithIntrinsicBounds(null, if (pageIndex == 2) page2IconActive else page2IconInactive, null, null)
        other?.setCompoundDrawablesWithIntrinsicBounds(null, if (pageIndex == 3) page3IconActive else page3IconInactive, null, null)

        trainings?.setTextColor(if (pageIndex == 0) colorActive else colorInactive)
        statistics?.setTextColor(if (pageIndex == 1) colorActive else colorInactive)
        settings?.setTextColor(if (pageIndex == 2) colorActive else colorInactive)
        other?.setTextColor(if (pageIndex == 3) colorActive else colorInactive)
    }
    //</editor-fold>


    companion object {
        private const val LOG_TAG = "FunctionsActivity"
        private const val PAGE_INDEX = "pageIndex"
    }
}