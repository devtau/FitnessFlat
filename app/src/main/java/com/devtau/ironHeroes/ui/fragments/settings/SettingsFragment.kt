package com.devtau.ironHeroes.ui.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.ui.DependencyRegistry
import com.devtau.ironHeroes.ui.fragments.ViewSubscriberFragment

class SettingsFragment: ViewSubscriberFragment(), SettingsView {

    lateinit var presenter: SettingsPresenter


    //<editor-fold desc="Framework overrides">
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DependencyRegistry().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_settings, container, false)
        initUi(root)
        return root
    }

    override fun onStop() {
        super.onStop()
        presenter.onStop()
    }
    //</editor-fold>


    //<editor-fold desc="View overrides">
    override fun getLogTag() = LOG_TAG
    //</editor-fold>


    //<editor-fold desc="Private methods">
    private fun initUi(root: View) {
        val showChampionFilter = root.findViewById<CheckBox>(R.id.showChampionFilter)
        showChampionFilter?.isChecked = presenter.isChampionFilterNeeded()
        showChampionFilter?.setOnCheckedChangeListener { _, isChecked ->
            presenter.showChampionFilterClicked(isChecked)
        }
        val showHeroFilter = root.findViewById<CheckBox>(R.id.showHeroFilter)
        showHeroFilter?.isChecked = presenter.isHeroFilterNeeded()
        showHeroFilter?.setOnCheckedChangeListener { _, isChecked ->
            presenter.showHeroFilterClicked(isChecked)
        }
    }
    //</editor-fold>


    companion object {
        const val FRAGMENT_TAG = "com.devtau.ironHeroes.ui.fragments.settings.SettingsFragment"
        private const val LOG_TAG = "SettingsFragment"

        fun newInstance(): SettingsFragment {
            val fragment = SettingsFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}