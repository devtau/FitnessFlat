package com.devtau.ironHeroes.ui.fragments.statistics

import android.content.Context
import android.graphics.PorterDuff
import androidx.core.content.ContextCompat
import com.devtau.ironHeroes.R
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import kotlinx.android.synthetic.main.custom_marker_view.view.*

//Entry should contain Object tag of type Integer with background color

class CustomMarkerView(context: Context, layoutResource: Int) :
    MarkerView(context, layoutResource) {

    constructor(context: Context): this(context, R.layout.custom_marker_view)


    init {
        setOffset((-(width / 2)).toFloat(), (-height).toFloat())
    }

    override fun refreshContent(entry: Entry, highlight: Highlight?) {
        val msg = "Entry should contain Object tag of type Integer with background color"
        if (entry.data == null) throw NullPointerException(msg)
        if (entry.data !is Tag) throw ClassCastException(msg)

        val tag = entry.data as Tag
        content.text = tag.title
        val markerColor = ContextCompat.getColor(context, tag.markerColorId)
        container.background.mutate().setColorFilter(markerColor, PorterDuff.Mode.SRC_ATOP)
    }
}