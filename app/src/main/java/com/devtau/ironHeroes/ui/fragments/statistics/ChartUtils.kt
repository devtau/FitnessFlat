package com.devtau.ironHeroes.ui.fragments.statistics

import android.content.Context
import android.view.View
import android.widget.TextView
import com.devtau.ironHeroes.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import timber.log.Timber
import java.util.*

object ChartUtils {

    fun initChart(context: Context?, chart: LineChart?, selected: TextView?,
                  lineData: LineData?, xLabels: List<Calendar>, xLabelsCount: Int,
                  listener: OnChartBalloonClickedListener
    ) {
        context ?: return
        chart ?: return
        chart.description.isEnabled = false
        chart.setTouchEnabled(true)
        chart.isDragEnabled = true
        chart.setScaleEnabled(true)
        chart.setPinchZoom(true)// if disabled, scaling can be done on x- and y-axis separately
        chart.setDrawGridBackground(false)
        chart.maxHighlightDistance = 300f
        chart.legend.isEnabled = false
        chart.marker = CustomMarkerView(context, R.layout.custom_marker_view)
        closeHighlight(
            chart,
            selected
        )

        chart.setOnChartValueSelectedListener(object: OnChartValueSelectedListener {
            var trainingId: Long? = null
            var exerciseId: Long? = null
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                val tag = e?.data as Tag?
                trainingId = tag?.trainingId
                exerciseId = tag?.exerciseInTrainingId
                selected?.visibility = View.VISIBLE
                val exerciseName = tag?.title?.replace("\n", " ")
                selected?.text = context.getString(R.string.selected_formatter, exerciseName)
                Timber.d("onValueSelected. trainingId=$trainingId, exerciseId=$exerciseId")
            }
            override fun onNothingSelected() {
                closeHighlight(chart, selected)
                listener.onBalloonClicked(trainingId, exerciseId)
            }
        })

        tuneXAxis(
            chart,
            xLabels,
            xLabelsCount,
            context.getColor(R.color.colorBlack)
        )
        tuneYAxis(
            chart,
            context.getColor(R.color.colorBlack),
            StatisticsFragment.Y_AXIS_MINIMUM
        )
        chart.extraBottomOffset = chart.rendererXAxis.paintAxisLabels.textSize
        chart.axisRight?.isEnabled = false
        chart.data = lineData
        chart.invalidate()
    }

    private fun tuneXAxis(chart: LineChart, xLabels: List<Calendar>, xLabelsCount: Int, axisTextColor: Int) {
        val xAxis = chart.xAxis
        xAxis.isEnabled = true
        xAxis.labelCount = xLabelsCount
        xAxis.textColor = axisTextColor
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.isGranularityEnabled = true
        xAxis.granularity = 1.0f
        xAxis.setDrawGridLines(true)

        chart.setXAxisRenderer(
            CustomXAxisRenderer(
                chart.viewPortHandler,
                xAxis,
                chart.getTransformer(YAxis.AxisDependency.LEFT),
                xLabels
            )
        )
    }

    private fun tuneYAxis(chart: LineChart, axisTextColor: Int, axisMinimum: Int) {
        val yAxis = chart.axisLeft
        yAxis.labelCount = StatisticsFragment.Y_LABELS_COUNT
        yAxis.textColor = axisTextColor
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        yAxis.setDrawGridLines(true)
        yAxis.axisMinimum = axisMinimum.toFloat()
    }

    private fun closeHighlight(chart: LineChart?, selected: TextView?) {
        chart?.highlightValues(null)
        selected?.visibility = View.INVISIBLE
        selected?.text = ""
    }
}

interface OnChartBalloonClickedListener {
    fun onBalloonClicked(trainingId: Long?, exerciseInTrainingId: Long?)
}