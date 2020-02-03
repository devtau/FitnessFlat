package com.devtau.ironHeroes.adapters.viewHolders

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.devtau.ironHeroes.R

class ExercisesInTrainingViewHolder(val root: View): RecyclerView.ViewHolder(root) {
    val context: Context
        get() = root.context
    val position: TextView = root.findViewById(R.id.position)
    val exercise: TextView = root.findViewById(R.id.exercise)
    val weight: TextView = root.findViewById(R.id.weight)
    val repeats: TextView = root.findViewById(R.id.repeats)
    val count: TextView = root.findViewById(R.id.count)
}