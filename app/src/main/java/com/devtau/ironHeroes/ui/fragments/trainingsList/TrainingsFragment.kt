package com.devtau.ironHeroes.ui.fragments.trainingsList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.devtau.ironHeroes.adapters.TrainingsAdapter
import com.devtau.ironHeroes.databinding.FragmentTrainingsBinding
import com.devtau.ironHeroes.ui.fragments.BaseFragment
import com.devtau.ironHeroes.util.EventObserver

class TrainingsFragment: BaseFragment() {

    private val _viewModel by viewModels<TrainingsViewModel> { getViewModelFactory() }


    //<editor-fold desc="Framework overrides">
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentTrainingsBinding.inflate(inflater, container, false).apply {
            viewModel = _viewModel
            content.viewModel = _viewModel
            lifecycleOwner = viewLifecycleOwner
            _viewModel.subscribeToVM(this)

            fab.postDelayed({ fab.show() }, android.R.integer.config_mediumAnimTime.toLong())
            content.listView.adapter = TrainingsAdapter(_viewModel)
        }
        return binding.root
    }
    //</editor-fold>


    //<editor-fold desc="Private methods">
    private fun TrainingsViewModel.subscribeToVM(binding: FragmentTrainingsBinding) {
        snackbarText.observe(viewLifecycleOwner, ::tryToShowSnackbar)

        openTrainingEvent.observe(viewLifecycleOwner, EventObserver {
            launchTrainingDetails(binding.content.listView, it)
        })
    }
    //</editor-fold>
}