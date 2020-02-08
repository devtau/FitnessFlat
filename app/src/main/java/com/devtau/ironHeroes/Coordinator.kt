package com.devtau.ironHeroes

import android.content.Context
import androidx.fragment.app.FragmentManager
import com.devtau.ironHeroes.enums.HumanType
import com.devtau.ironHeroes.ui.fragments.other.OtherFragment
import com.devtau.ironHeroes.ui.fragments.settings.SettingsFragment
import com.devtau.ironHeroes.ui.fragments.statistics.StatisticsFragment
import com.devtau.ironHeroes.ui.fragments.trainingsList.TrainingsFragment

interface Coordinator {
    fun launchHeroesActivity(context: Context?, humanType: HumanType)
    fun launchHeroDetailsActivity(context: Context?, heroId: Long?, humanType: HumanType)
    fun launchTrainingDetailsActivity(context: Context?, trainingId: Long?)

    fun showExerciseDialog(fragmentManager: FragmentManager?, heroId: Long?, trainingId: Long?,
                           exerciseInTrainingId: Long?, position: Int? = null)

    fun newOtherFragmentInstance(): OtherFragment
    fun newSettingsFragmentInstance(): SettingsFragment
    fun newStatisticsFragmentInstance(): StatisticsFragment
    fun newTrainingsFragmentInstance(): TrainingsFragment
}