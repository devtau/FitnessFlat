package com.devtau.ironHeroes.ui.activities.trainingDetails

import android.content.Context
import com.devtau.ironHeroes.enums.HumanType
import io.reactivex.functions.Action

interface TrainingDetailsPresenter {
    fun onStop()
    fun restartLoaders()
    fun updateTrainingData(championId: Long?, heroId: Long?, date: Long?)
    fun showDateDialog(context: Context, selectedDate: Long?)
    fun onBackPressed(action: Action)
    fun deleteTraining()
}