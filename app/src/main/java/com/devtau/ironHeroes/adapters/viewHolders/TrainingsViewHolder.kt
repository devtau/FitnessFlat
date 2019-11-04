package com.devtau.ironHeroes.adapters.viewHolders

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.devtau.ironHeroes.R

class TrainingsViewHolder(val root: View): RecyclerView.ViewHolder(root) {
    val context: Context
        get() = root.context

    val championImage: ImageView = root.findViewById(R.id.championImage)
    val date: TextView = root.findViewById(R.id.date)
    val heroImage: ImageView = root.findViewById(R.id.heroImage)
}