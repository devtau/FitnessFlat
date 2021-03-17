package com.devtau.ironHeroes.ui.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.devtau.ironHeroes.databinding.FragmentSettingsBinding
import com.devtau.ironHeroes.ui.fragments.BaseFragment

class SettingsFragment: BaseFragment() {

    private val _viewModel by viewModels<SettingsViewModel> { getViewModelFactory() }


    //<editor-fold desc="Framework overrides">
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentSettingsBinding.inflate(inflater, container, false).apply {
            viewModel = _viewModel
            lifecycleOwner = viewLifecycleOwner
            initUi()
        }
        return binding.root
    }
    //</editor-fold>


    //<editor-fold desc="Private methods">
    // This method call is needed because of a problem in ViewPager lifecycleOwner
    // Checkboxes checked states are not initialized correctly without this method call
    private fun FragmentSettingsBinding.initUi() {
        showChampionFilter.isChecked = _viewModel.showChampionFilter
        showHeroFilter.isChecked = _viewModel.showHeroFilter
        openEditDialogFromStatistics.isChecked = _viewModel.openEditDialogFromStatistics
    }
    //</editor-fold>
}