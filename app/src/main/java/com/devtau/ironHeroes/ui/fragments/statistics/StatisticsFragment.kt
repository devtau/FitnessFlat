package com.devtau.ironHeroes.ui.fragments.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.devtau.ironHeroes.databinding.FragmentStatisticsBinding
import com.devtau.ironHeroes.ui.fragments.BaseFragment
import com.devtau.ironHeroes.util.EventObserver
import timber.log.Timber

class StatisticsFragment: BaseFragment() {

    private val _viewModel by viewModels<StatisticsViewModel> { getViewModelFactory() }


    //<editor-fold desc="Framework overrides">
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentStatisticsBinding.inflate(inflater, container, false).apply {
            viewModel = _viewModel
            lifecycleOwner = viewLifecycleOwner
            _viewModel.subscribeToVM(this)
        }
        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        Timber.d("onSaveInstanceState")
        super.onSaveInstanceState(outState)
    }
    //</editor-fold>


    //<editor-fold desc="Interface overrides">
    //</editor-fold>


    //<editor-fold desc="Private methods">
    private fun StatisticsViewModel.subscribeToVM(binding: FragmentStatisticsBinding) {
        snackbarText.observe(viewLifecycleOwner, ::tryToShowSnackbar)

        showStatisticsData.observe(viewLifecycleOwner, {
            Timber.d("showStatisticsData. lineData=${it.lineData}")
            ChartUtils.initChart(context, binding.chart, binding.selected, it.lineData,
                it.xLabels, it.xLabelsCount, onBalloonClickedListener)
        })

        showExerciseDetails.observe(viewLifecycleOwner, EventObserver {
            showExerciseFromStatistics(view, it)
        })

        exercises.observe(viewLifecycleOwner, {/*NOP*/})

        exercisesInTrainings.observe(viewLifecycleOwner, {/*NOP*/})
    }
    //</editor-fold>


    companion object {
        const val Y_LABELS_COUNT = 5
        const val Y_AXIS_MINIMUM = 0
    }
}