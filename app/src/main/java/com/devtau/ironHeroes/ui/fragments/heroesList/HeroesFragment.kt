package com.devtau.ironHeroes.ui.fragments.heroesList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.devtau.ironHeroes.adapters.HeroesAdapter
import com.devtau.ironHeroes.databinding.FragmentHeroesBinding
import com.devtau.ironHeroes.ui.fragments.BaseFragment
import com.devtau.ironHeroes.ui.fragments.getViewModelFactory
import com.devtau.ironHeroes.ui.fragments.initActionBar
import com.devtau.ironHeroes.util.Constants
import com.devtau.ironHeroes.util.EventObserver

class HeroesFragment: BaseFragment() {

    private val _viewModel by viewModels<HeroesListViewModel> { getViewModelFactory() }


    //<editor-fold desc="Framework overrides">
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentHeroesBinding.inflate(inflater, container, false).apply {
            viewModel = _viewModel
            lifecycleOwner = viewLifecycleOwner
            _viewModel.subscribeToVM(this)

            fab.postDelayed({ fab.show() }, Constants.STANDARD_DELAY_MS)
            listView.adapter = HeroesAdapter(_viewModel)
        }
        return binding.root
    }
    //</editor-fold>


    //<editor-fold desc="Private methods">
    private fun HeroesListViewModel.subscribeToVM(binding: FragmentHeroesBinding) {
        toolbarTitle.observe(viewLifecycleOwner, EventObserver {
            activity?.initActionBar(it)
        })
        openHeroEvent.observe(viewLifecycleOwner, EventObserver {
            coordinator.launchHeroDetails(binding.listView, it.heroId, it.humanType)
        })
    }
    //</editor-fold>


    companion object {
        private const val LOG_TAG = "HeroesFragment"
    }
}