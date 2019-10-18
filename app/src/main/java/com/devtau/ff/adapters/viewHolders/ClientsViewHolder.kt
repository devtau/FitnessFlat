package com.devtau.ff.adapters.viewHolders

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.devtau.ff.R

class ClientsViewHolder(val root: View): RecyclerView.ViewHolder(root) {
    val context: Context
        get() = root.context
    val image: ImageView = root.findViewById(R.id.image)
    val name: TextView = root.findViewById(R.id.name)
}