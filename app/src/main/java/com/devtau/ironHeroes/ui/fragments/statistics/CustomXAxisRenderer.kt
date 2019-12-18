package com.devtau.ironHeroes.ui.fragments.statistics

import android.graphics.Canvas
import com.devtau.ironHeroes.util.AppUtils
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.renderer.XAxisRenderer
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler

class CustomXAxisRenderer(viewPortHandler: ViewPortHandler, xAxis: XAxis, trans: Transformer):
    XAxisRenderer(viewPortHandler, xAxis, trans) {

    override fun drawLabel(c: Canvas, formattedLabel: String, x: Float, y: Float, anchor: MPPointF, angleDegrees: Float) {
        val lines = formattedLabel.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val formattedDate = AppUtils.formatShortDate(lines[0])
        Utils.drawXAxisValue(c, formattedDate, x, y, mAxisLabelPaint, anchor, angleDegrees)
        if (lines.size > 1) Utils.drawXAxisValue(c, lines[1], x, y + mAxisLabelPaint.textSize, mAxisLabelPaint, anchor, angleDegrees)
    }
}
