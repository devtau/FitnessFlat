package com.devtau.ironHeroes.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.devtau.ironHeroes.ui.fragments.other.OtherFragment
import com.devtau.ironHeroes.ui.fragments.settings.SettingsFragment
import com.devtau.ironHeroes.ui.fragments.statistics.StatisticsFragment
import com.devtau.ironHeroes.ui.fragments.trainingsList.TrainingsFragment
import timber.log.Timber

class FunctionsPagerAdapter(host: Fragment): FragmentStateAdapter(host) {

    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        Timber.d("getItem. position=$position")
        return when (position) {
            0 -> TrainingsFragment()
            1 -> StatisticsFragment()
            2 -> SettingsFragment()
            3 -> OtherFragment()
            else -> OtherFragment()
        }
    }
}