package com.devtau.ironHeroes.ui.fragments.trainingsList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.devtau.ironHeroes.adapters.TrainingsAdapter
import com.devtau.ironHeroes.databinding.FragmentTrainingsBinding
import com.devtau.ironHeroes.ui.fragments.BaseFragment
import com.devtau.ironHeroes.ui.fragments.getViewModelFactory
import com.devtau.ironHeroes.util.Constants
import com.devtau.ironHeroes.util.EventObserver
import com.devtau.ironHeroes.util.setupSnackbar

class TrainingsFragment: BaseFragment() {

    private val _viewModel by viewModels<TrainingsViewModel> { getViewModelFactory() }


    //<editor-fold desc="Framework overrides">
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentTrainingsBinding.inflate(inflater, container, false).apply {
            viewModel = _viewModel
            lifecycleOwner = viewLifecycleOwner
            _viewModel.subscribeToVM(this)

            fab.postDelayed({ fab.show() }, Constants.STANDARD_DELAY_MS)
            listView.adapter = TrainingsAdapter(_viewModel)
        }
        return binding.root
    }
    //</editor-fold>


    //<editor-fold desc="Private methods">
    private fun TrainingsViewModel.subscribeToVM(binding: FragmentTrainingsBinding) {
        view?.setupSnackbar(viewLifecycleOwner, snackbarText)

        openTrainingEvent.observe(viewLifecycleOwner, EventObserver {
            coordinator.launchTrainingDetails(binding.listView, it)
        })
    }
    //</editor-fold>


    companion object {
        private const val LOG_TAG = "TrainingsFragment"
    }
}