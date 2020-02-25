package com.devtau.ironHeroes.ui.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.databinding.FragmentSettingsBinding
import com.devtau.ironHeroes.ui.DependencyRegistry
import com.devtau.ironHeroes.ui.fragments.BaseFragment
import com.devtau.ironHeroes.util.PreferencesManager

class SettingsFragment: BaseFragment(), SettingsContract.View {

    private lateinit var presenter: SettingsContract.Presenter


    //<editor-fold desc="Framework overrides">
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DependencyRegistry.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<FragmentSettingsBinding>(inflater, R.layout.fragment_settings, container, false)
        binding.model = PreferencesManager
        binding.initUi()
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        presenter.restartLoaders()
    }

    override fun onStop() {
        super.onStop()
        presenter.onStop()
    }
    //</editor-fold>


    //<editor-fold desc="Interface overrides">
    override fun getLogTag() = LOG_TAG
    override fun initActionbar() = false
    //</editor-fold>


    fun configureWith(presenter: SettingsContract.Presenter) {
        this.presenter = presenter
    }


    //<editor-fold desc="Private methods">
    private fun FragmentSettingsBinding.initUi() = apply {
        showChampionFilter.isChecked = presenter.isChampionFilterNeeded()
        showChampionFilter.setOnCheckedChangeListener { _, isChecked ->
            presenter.showChampionFilterClicked(isChecked)
        }
        showHeroFilter.isChecked = presenter.isHeroFilterNeeded()
        showHeroFilter.setOnCheckedChangeListener { _, isChecked ->
            presenter.showHeroFilterClicked(isChecked)
        }
        openEditDialogFromStatistics.isChecked = presenter.isEditDialogNeeded()
        openEditDialogFromStatistics.setOnCheckedChangeListener { _, isChecked ->
            presenter.openEditDialogFromStatisticsClicked(isChecked)
        }
//        invalidateAll()
    }
    //</editor-fold>


    companion object {
        private const val LOG_TAG = "SettingsFragment"
    }
}