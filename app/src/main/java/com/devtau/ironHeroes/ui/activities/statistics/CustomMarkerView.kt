package com.devtau.ironHeroes.ui.activities.statistics

import android.content.Context
import android.graphics.PorterDuff
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.devtau.ironHeroes.R
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight

//Entry should contain Object tag of type Integer with background color

class CustomMarkerView(context: Context, layoutResource: Int) :
    MarkerView(context, layoutResource) {

    constructor(context: Context): this(context, R.layout.custom_marker_view)

    private val container: View = findViewById(R.id.container)
    private val content: TextView = findViewById(R.id.content)


    init {
        setOffset((-(width / 2)).toFloat(), (-height).toFloat())
    }

    override fun refreshContent(entry: Entry, highlight: Highlight?) {
        val msg = "Entry should contain Object tag of type Integer with background color"
        if (entry.data == null) throw NullPointerException(msg)
        if (entry.data !is Int) throw ClassCastException(msg)

        content.text = entry.y.toInt().toString()
        val lineColor = ContextCompat.getColor(context, entry.data as Int)
        container.background.mutate().setColorFilter(lineColor, PorterDuff.Mode.SRC_ATOP)
    }
}