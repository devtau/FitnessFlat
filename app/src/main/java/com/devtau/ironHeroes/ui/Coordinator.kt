package com.devtau.ironHeroes.ui

import android.content.Context
import android.view.View
import com.devtau.ironHeroes.enums.HumanType

interface Coordinator {
    fun launchHeroes(view: View?, humanType: HumanType)
    fun launchHeroDetails(view: View?, heroId: Long?, humanType: HumanType)
    fun launchTrainingDetails(view: View?, trainingId: Long)
    fun launchDBViewer(context: Context?)

    fun showExercise(view: View?, heroId: Long?, trainingId: Long?,
                     exerciseInTrainingId: Long?, position: Int? = null)
}