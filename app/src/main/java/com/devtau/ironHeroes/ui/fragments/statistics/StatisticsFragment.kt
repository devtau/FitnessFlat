package com.devtau.ironHeroes.ui.fragments.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.devtau.ironHeroes.databinding.FragmentStatisticsBinding
import com.devtau.ironHeroes.ui.ResourceResolver
import com.devtau.ironHeroes.ui.fragments.BaseFragment
import com.devtau.ironHeroes.ui.fragments.getViewModelFactory
import com.devtau.ironHeroes.util.EventObserver
import com.devtau.ironHeroes.util.Logger
import com.devtau.ironHeroes.util.setupSnackbar

class StatisticsFragment: BaseFragment() {

    private val resourceResolver = object: ResourceResolver {
        override fun resolveColor(@ColorRes colorResId: Int): Int = context?.getColor(colorResId) ?: 0

        override fun resolveString(@StringRes stringResId: Int): String = context?.getString(stringResId) ?: ""
    }

    private val _viewModel by viewModels<StatisticsViewModel> { getViewModelFactory(resourceResolver) }


    //<editor-fold desc="Framework overrides">
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Logger.d(LOG_TAG, "onCreateView")
        val binding = FragmentStatisticsBinding.inflate(inflater, container, false).apply {
            viewModel = _viewModel
            lifecycleOwner = viewLifecycleOwner
            _viewModel.subscribeToVM(this)
        }
        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        Logger.d(LOG_TAG, "onSaveInstanceState")
        super.onSaveInstanceState(outState)
    }
    //</editor-fold>


    //<editor-fold desc="Interface overrides">
    //</editor-fold>


    //<editor-fold desc="Private methods">
    private fun StatisticsViewModel.subscribeToVM(binding: FragmentStatisticsBinding) {
        view?.setupSnackbar(viewLifecycleOwner, snackbarText)

        showStatisticsData.observe(viewLifecycleOwner, Observer {
            Logger.d(LOG_TAG, "showStatisticsData. lineData=${it.lineData}")
            ChartUtils.initChart(context, binding.chart, binding.selected, it.lineData,
                it.xLabels, it.xLabelsCount, onBalloonClickedListener)
        })

        showExerciseDetails.observe(viewLifecycleOwner, EventObserver {
            coordinator.showExerciseFromStatistics(view, it)
        })

        exercises.observe(viewLifecycleOwner, Observer {/*NOP*/})

        exercisesInTrainings.observe(viewLifecycleOwner, Observer {/*NOP*/})
    }
    //</editor-fold>


    companion object {
        private const val LOG_TAG = "StatisticsFragment"
        const val Y_LABELS_COUNT = 5
        const val Y_AXIS_MINIMUM = 0
    }
}