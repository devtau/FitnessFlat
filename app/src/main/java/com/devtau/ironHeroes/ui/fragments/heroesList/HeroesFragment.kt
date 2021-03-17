package com.devtau.ironHeroes.ui.fragments.heroesList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.devtau.ironHeroes.adapters.HeroesAdapter
import com.devtau.ironHeroes.databinding.FragmentHeroesBinding
import com.devtau.ironHeroes.ui.fragments.BaseFragment
import com.devtau.ironHeroes.util.EventObserver

class HeroesFragment: BaseFragment() {

    private val _viewModel by viewModels<HeroesViewModel> { getViewModelFactory() }


    //<editor-fold desc="Framework overrides">
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentHeroesBinding.inflate(inflater, container, false).apply {
            viewModel = _viewModel
            lifecycleOwner = viewLifecycleOwner
            _viewModel.subscribeToVM(this)

            fab.postDelayed({ fab.show() }, android.R.integer.config_mediumAnimTime.toLong())
            listView.adapter = HeroesAdapter(_viewModel)
        }
        return binding.root
    }
    //</editor-fold>


    //<editor-fold desc="Private methods">
    private fun HeroesViewModel.subscribeToVM(binding: FragmentHeroesBinding) {
        openHeroEvent.observe(viewLifecycleOwner, EventObserver {
            launchHeroDetails(binding.listView, it.heroId, it.humanType)
        })
    }
    //</editor-fold>
}