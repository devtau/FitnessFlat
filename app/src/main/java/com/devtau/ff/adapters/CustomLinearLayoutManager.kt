package com.devtau.ff.adapters

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devtau.ff.util.Logger

class CustomLinearLayoutManager(context: Context): LinearLayoutManager(context) {

    private val logTag = "CustomLinearLayoutManager"

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State) {
        try {
            super.onLayoutChildren(recycler, state)
        } catch (e: IndexOutOfBoundsException) {
            Logger.w(logTag, "meet a IOOBE in RecyclerView")
        }
    }
}
