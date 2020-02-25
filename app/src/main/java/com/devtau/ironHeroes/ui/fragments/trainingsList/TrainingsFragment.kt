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
import com.devtau.ironHeroes.util.PreferencesManager
import com.devtau.ironHeroes.util.setupSnackbar
import kotlinx.android.synthetic.main.fragment_trainings.*

class TrainingsFragment: BaseFragment() {

    private val _viewModel by viewModels<TrainingsViewModel> { getViewModelFactory() }
    private lateinit var binding: FragmentTrainingsBinding
    private val prefs = PreferencesManager


    //<editor-fold desc="Framework overrides">
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        binding = FragmentTrainingsBinding.inflate(inflater, container, false).apply {
            viewModel = _viewModel
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.fab.postDelayed({ binding.fab.show() }, Constants.STANDARD_DELAY_MS)
        binding.listView.adapter = TrainingsAdapter(_viewModel)
        setupNavigation()
        view?.setupSnackbar(viewLifecycleOwner, _viewModel.snackbarText)
    }
    //</editor-fold>


    //<editor-fold desc="Interface overrides">
    override fun getLogTag() = LOG_TAG
    override fun initActionbar() = false
    //</editor-fold>


    //<editor-fold desc="Private methods">
    private fun setupNavigation() {
        _viewModel.openTrainingEvent.observe(viewLifecycleOwner, EventObserver {
            launchTrainingDetails(listView, it)
        })
    }
    //</editor-fold>


    companion object {
        private const val LOG_TAG = "TrainingsFragment"
    }
}