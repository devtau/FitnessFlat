package com.devtau.ironHeroes.ui.activities.statistics

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.enums.StatisticsType
import com.devtau.ironHeroes.ui.DependencyRegistry
import com.devtau.ironHeroes.ui.activities.ViewSubscriberActivity
import com.devtau.ironHeroes.ui.dialogs.exerciseDialog.ExerciseDialog
import com.devtau.ironHeroes.util.AppUtils
import com.devtau.ironHeroes.util.Constants.HERO_ID
import com.devtau.ironHeroes.util.Logger
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.activity_statistics.*

class StatisticsActivity: ViewSubscriberActivity(),
    StatisticsView, ExerciseDialog.Listener {

    lateinit var presenter: StatisticsPresenter
    private var statisticsType: StatisticsType? = null


    //<editor-fold desc="Framework overrides">
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)
        DependencyRegistry().inject(this)
        AppUtils.initToolbar(this, R.string.statistics, true)
    }

    override fun onStart() {
        super.onStart()
        presenter.restartLoaders()
        subscribeField(muscleGroup, Consumer { applyFilter() })
        subscribeField(exercise, Consumer { applyFilter() })
    }

    override fun onStop() {
        presenter.onStop()
        super.onStop()
    }
    //</editor-fold>


    //<editor-fold desc="View overrides">
    override fun getLogTag() = LOG_TAG
    override fun showMuscleGroups(list: List<String>?, selectedIndex: Int) =
        AppUtils.initSpinner(muscleGroup, list, selectedIndex, this)
    override fun showExercises(list: List<String>?, selectedIndex: Int) =
        AppUtils.initSpinner(exercise, list, selectedIndex, this)

    override fun showStatisticsData(lineData: LineData?) {
        Logger.d(LOG_TAG, "showStatisticsData. lineData=$lineData")
        initChart(lineData)
    }

    override fun showExerciseDetails(heroId: Long?, trainingId: Long?, exerciseInTrainingId: Long?) {
        Logger.d(LOG_TAG, "showExerciseDetails. heroId=$heroId, trainingId=$trainingId, exerciseInTrainingId=$exerciseInTrainingId")
        ExerciseDialog.showDialog(supportFragmentManager, heroId, trainingId, exerciseInTrainingId, this@StatisticsActivity)
    }
    //</editor-fold>


    //<editor-fold desc="Private methods">
    private fun initChart(lineData: LineData?) {
//        chart.setViewPortOffsets(0f, 0f, 0f, 0f)
//        chart.setBackgroundColor(Color.rgb(104, 241, 175))
        chart.description.isEnabled = false
        chart.setTouchEnabled(true)
        chart.isDragEnabled = true
        chart.setScaleEnabled(false)
        chart.setPinchZoom(false)// if disabled, scaling can be done on x- and y-axis separately
        chart.setDrawGridBackground(false)
        chart.maxHighlightDistance = 300f
        chart.legend.isEnabled = false
        chart.marker = CustomMarkerView(this, R.layout.custom_marker_view)
        chart.highlightValues(null)
        chart.setOnChartValueSelectedListener(object: OnChartValueSelectedListener {
            var trainingId: Long? = null
            var exerciseId: Long? = null
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                trainingId = (e?.data as Tag?)?.trainingId
                exerciseId = (e?.data as Tag?)?.exerciseInTrainingId
                Logger.d(LOG_TAG, "onValueSelected. trainingId=$trainingId, exerciseId=$exerciseId")
            }
            override fun onNothingSelected() = presenter.onBalloonClicked(trainingId, exerciseId)
        })

        tuneXAxis(X_LABELS_COUNT, resolveColor(R.color.colorBlack))
        tuneYAxis(resolveColor(R.color.colorBlack), Y_AXIS_MINIMUM)
        chart.extraBottomOffset = chart.rendererXAxis.paintAxisLabels.textSize
        chart.axisRight.isEnabled = false
        chart.data = lineData
//        chart.animateXY(400, 400)
        chart.invalidate()
    }

    private fun tuneAxes(xLabelsCount: Int, axisTextColor: Int, yAxisMinimum: Int) {
        tuneXAxis(xLabelsCount, axisTextColor)
        tuneYAxis(axisTextColor, yAxisMinimum)
        chart.extraBottomOffset = chart.rendererXAxis.paintAxisLabels.textSize
        chart.axisRight.isEnabled = false
    }

    private fun tuneXAxis(labelsCount: Int, axisTextColor: Int) {
        val xAxis = chart.xAxis
        xAxis.isEnabled = true
        xAxis.labelCount = labelsCount
        xAxis.textColor = axisTextColor
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.isGranularityEnabled = true
        xAxis.granularity = 1.0f
        xAxis.setDrawGridLines(false)

        chart.setXAxisRenderer(CustomXAxisRenderer(chart.viewPortHandler, xAxis, chart.getTransformer(YAxis.AxisDependency.LEFT)))
//        xAxis.axisLineColor = Color.TRANSPARENT
//        xAxis.axisMinimum = -1f
//        xAxis.axisMaximum = labelsCount.toFloat()
//        xAxis.valueFormatter = object: ValueFormatter() {
//            override fun getFormattedValue(value: Float, axis: AxisBase?) =
//                statisticsType?.getFormattedValue(this@StatisticsActivity, value.toInt())
//        }
    }

    private fun tuneYAxis(axisTextColor: Int, axisMinimum: Int) {
        val yAxis = chart.axisLeft
        yAxis.labelCount = Y_LABELS_COUNT
        yAxis.textColor = axisTextColor
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        yAxis.setDrawGridLines(true)
        yAxis.axisMinimum = axisMinimum.toFloat()
//        yAxis.valueFormatter = object: ValueFormatter() {
//            override fun getFormattedValue(value: Float, axis: AxisBase?) = if (value == 0f) "" else value.toInt().toString()
//        }
//        yAxis.axisLineColor = Color.TRANSPARENT
//        yAxis.xOffset = -5f
//        yAxis.typeface = someTypeface
    }

    private fun applyFilter() {
        presenter.filterAndUpdateChart(muscleGroup?.selectedItemPosition ?: 0, exercise?.selectedItemPosition ?: 0)
    }
    //</editor-fold>


    companion object {
        private const val LOG_TAG = "StatisticsActivity"
        private const val X_LABELS_COUNT = 10
        private const val Y_LABELS_COUNT = 5
        private const val Y_AXIS_MINIMUM = 0

        fun newInstance(context: Context, heroId: Long) {
            val intent = Intent(context, StatisticsActivity::class.java)
            intent.putExtra(HERO_ID, heroId)
            context.startActivity(intent)
        }
    }
}