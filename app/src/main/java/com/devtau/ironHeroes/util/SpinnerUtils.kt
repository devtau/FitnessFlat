package com.devtau.ironHeroes.util

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.devtau.ironHeroes.data.model.Exercise
import com.devtau.ironHeroes.data.model.Hero
import com.devtau.ironHeroes.data.model.MuscleGroup
import java.util.*

object SpinnerUtils {

    private const val LOG_TAG = "SpinnerUtils"


    fun initSpinner(spinner: Spinner?, spinnerStrings: List<String>?, selectedIndex: Int, context: Context?) {
        if (spinner == null || spinnerStrings == null || context == null) {
            Logger.e(LOG_TAG, "initSpinner. bad data. aborting")
            return
        }
        var adapter = spinner.adapter as ArrayAdapter<String>?
        if (adapter == null) {
            adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, spinnerStrings)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        } else {
            adapter.clear()
            adapter.addAll(spinnerStrings)
            adapter.notifyDataSetChanged()
        }
        spinner.setSelection(selectedIndex)
    }

    fun getHeroesSpinnerStrings(list: List<Hero>, withEmptyString: Boolean = false): List<String> {
        val spinnerStrings = ArrayList<String>()
        if (withEmptyString) spinnerStrings.add("- -")
        for (next in list) spinnerStrings.add(next.getName())
        return spinnerStrings
    }

    fun getMuscleGroupsSpinnerStrings(list: List<MuscleGroup>, withEmptyString: Boolean = false): List<String> {
        val spinnerStrings = ArrayList<String>()
        if (withEmptyString) spinnerStrings.add("- -")
        for (next in list) spinnerStrings.add(next.name)
        return spinnerStrings
    }

    fun getExercisesSpinnerStrings(list: List<Exercise>, withEmptyString: Boolean = false): List<String> {
        val spinnerStrings = ArrayList<String>()
        if (withEmptyString) spinnerStrings.add("- -")
        for (next in list) spinnerStrings.add(next.name)
        return spinnerStrings
    }

    fun getSelectedHeroIndex(list: List<Hero>, selectedId: Long?): Int {
        var index = 0
        for ((i, next) in list.withIndex())
            if (next.id == selectedId)
                index = i
        return index
    }

    fun getSelectedExerciseIndex(list: List<Exercise>, selectedId: Long?): Int {
        var index = 0
        for ((i, next) in list.withIndex())
            if (next.id == selectedId)
                index = i
        return index
    }

    fun getSelectedMuscleGroupIndex(list: List<MuscleGroup>, selectedId: Long?): Int {
        var index = 0
        for ((i, next) in list.withIndex())
            if (next.id == selectedId)
                index = i
        return index
    }
}