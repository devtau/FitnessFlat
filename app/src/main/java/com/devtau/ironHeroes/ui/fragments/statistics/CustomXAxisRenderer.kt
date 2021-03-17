package com.devtau.ironHeroes.ui.fragments.statistics

import android.graphics.Canvas
import com.devtau.ironHeroes.util.DateUtils
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.renderer.XAxisRenderer
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler
import timber.log.Timber
import java.util.*

class CustomXAxisRenderer(
    viewPortHandler: ViewPortHandler,
    xAxis: XAxis,
    trans: Transformer,
    private val xLabels: List<Calendar>
): XAxisRenderer(viewPortHandler, xAxis, trans) {

    override fun drawLabel(c: Canvas, formattedLabel: String, x: Float, y: Float, anchor: MPPointF, angleDegrees: Float) {
        Timber.d("drawLabel. formattedLabel=$formattedLabel, x=$x, y=$y")
        val index = formattedLabel.toInt()
        val formattedDate = if (index in xLabels.indices) DateUtils.formatShortDate(xLabels[index]) else ""
        Utils.drawXAxisValue(c, formattedDate, x, y, mAxisLabelPaint, anchor, angleDegrees)
    }
}