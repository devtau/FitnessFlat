package com.devtau.ironHeroes.ui.fragments.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import android.widget.TextView
import com.devtau.ironHeroes.Coordinator
import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.ui.DependencyRegistry
import com.devtau.ironHeroes.ui.fragments.ViewSubscriberFragment
import com.devtau.ironHeroes.util.AppUtils
import com.devtau.ironHeroes.util.Logger
import com.devtau.ironHeroes.util.toast
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import io.reactivex.functions.Consumer

class StatisticsFragment: ViewSubscriberFragment(),
    StatisticsContract.View {

    private lateinit var presenter: StatisticsContract.Presenter
    private lateinit var coordinator: Coordinator

    private var selectedContainer: View? = null
    private var selected: TextView? = null
    private var chart: LineChart? = null
    private var muscleGroup: Spinner? = null
    private var exercise: Spinner? = null


    //<editor-fold desc="Framework overrides">
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DependencyRegistry.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_statistics, container, false)
        initUI(root)
        return root
    }

    override fun onStart() {
        super.onStart()
        presenter.restartLoaders()
        subscribeField(muscleGroup, Consumer { applyFilter(muscleGroup?.selectedItemPosition ?: 0, 0) })
        subscribeField(exercise, Consumer { selectedExerciseIndex ->
            val dataSets = chart?.lineData?.dataSets
            val selectedExerciseName = exercise?.getItemAtPosition(selectedExerciseIndex) as String
            var exerciseFound = false
            if (dataSets != null && dataSets.isNotEmpty()) {
                val showAll = selectedExerciseIndex == 0
                for (next in dataSets) {
                    val isCurrentExerciseSelected = next.label == selectedExerciseName
                    next.isVisible = showAll || isCurrentExerciseSelected
                    next.isHighlightEnabled = showAll || isCurrentExerciseSelected
                    if (!exerciseFound) exerciseFound = showAll || isCurrentExerciseSelected
                }
            }
            closeHighlight()
            chart?.invalidate()
            if (!exerciseFound) context?.toast(R.string.no_exercise_data)
        })
    }

    override fun onStop() {
        presenter.onStop()
        super.onStop()
    }
    //</editor-fold>


    //<editor-fold desc="View overrides">
    override fun getLogTag() = LOG_TAG
    override fun showMuscleGroups(list: List<String>?, selectedIndex: Int) =
        AppUtils.initSpinner(muscleGroup, list, selectedIndex, context)
    override fun showExercises(list: List<String>?, selectedIndex: Int) =
        AppUtils.initSpinner(exercise, list, selectedIndex, context)

    override fun showStatisticsData(lineData: LineData?) {
        Logger.d(LOG_TAG, "showStatisticsData. lineData=$lineData")
        initChart(lineData)
    }

    override fun showExerciseDetails(heroId: Long?, trainingId: Long?, exerciseInTrainingId: Long?) {
        Logger.d(LOG_TAG, "showExerciseDetails. heroId=$heroId, trainingId=$trainingId, exerciseInTrainingId=$exerciseInTrainingId")
        coordinator.showExerciseDialog(childFragmentManager, heroId, trainingId, exerciseInTrainingId)
    }
    //</editor-fold>


    fun configureWith(presenter: StatisticsContract.Presenter, coordinator: Coordinator) {
        this.presenter = presenter
        this.coordinator = coordinator
    }


    //<editor-fold desc="Private methods">

    private fun initUI(root: View?) {
        selectedContainer = root?.findViewById(R.id.selectedContainer)
        selected = root?.findViewById(R.id.selected)
        chart = root?.findViewById(R.id.chart)
        muscleGroup = root?.findViewById(R.id.muscleGroup)
        exercise = root?.findViewById(R.id.exercise)
    }

    private fun initChart(lineData: LineData?) {
        val context = context ?: return
        val chart = chart ?: return
//        chart.setViewPortOffsets(0f, 0f, 0f, 0f)
//        chart.setBackgroundColor(Color.rgb(104, 241, 175))
        chart.description.isEnabled = false
        chart.setTouchEnabled(true)
        chart.isDragEnabled = true
        chart.setScaleEnabled(true)
        chart.setPinchZoom(true)// if disabled, scaling can be done on x- and y-axis separately
        chart.setDrawGridBackground(false)
        chart.maxHighlightDistance = 300f
        chart.legend.isEnabled = false
        chart.marker = CustomMarkerView(context, R.layout.custom_marker_view)
        closeHighlight()

        chart.setOnChartValueSelectedListener(object: OnChartValueSelectedListener {
            var trainingId: Long? = null
            var exerciseId: Long? = null
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                val tag = e?.data as Tag?
                trainingId = tag?.trainingId
                exerciseId = tag?.exerciseInTrainingId
                selectedContainer?.visibility = View.VISIBLE
                selected?.text = tag?.title?.replace("\n", " ")
                Logger.d(LOG_TAG, "onValueSelected. trainingId=$trainingId, exerciseId=$exerciseId")
            }
            override fun onNothingSelected() {
                closeHighlight()
                presenter.onBalloonClicked(trainingId, exerciseId)
            }
        })

        tuneXAxis(chart, X_LABELS_COUNT, resolveColor(R.color.colorBlack))
        tuneYAxis(chart, resolveColor(R.color.colorBlack), Y_AXIS_MINIMUM)
        chart.extraBottomOffset = chart.rendererXAxis.paintAxisLabels.textSize
        chart.axisRight?.isEnabled = false
        chart.data = lineData
//        chart.animateXY(400, 400)
        chart.invalidate()
    }

    private fun tuneXAxis(chart: LineChart, labelsCount: Int, axisTextColor: Int) {
        val xAxis = chart.xAxis
        xAxis.isEnabled = true
        xAxis.labelCount = labelsCount
        xAxis.textColor = axisTextColor
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.isGranularityEnabled = true
        xAxis.granularity = 1.0f
        xAxis.setDrawGridLines(true)

        chart.setXAxisRenderer(CustomXAxisRenderer(chart.viewPortHandler, xAxis, chart.getTransformer(YAxis.AxisDependency.LEFT)))
//        xAxis.axisLineColor = Color.TRANSPARENT
//        xAxis.axisMinimum = -1f
//        xAxis.axisMaximum = labelsCount.toFloat()
//        xAxis.valueFormatter = object: ValueFormatter() {
//            override fun getFormattedValue(value: Float, axis: AxisBase?) =
//                statisticsType?.getFormattedValue(this@StatisticsActivity, value.toInt())
//        }
    }

    private fun tuneYAxis(chart: LineChart, axisTextColor: Int, axisMinimum: Int) {
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

    private fun applyFilter(muscleGroupIndex: Int, exerciseIndex: Int) {
        Logger.d(LOG_TAG, "applyFilter. muscleGroupIndex=$muscleGroupIndex, exerciseIndex=$exerciseIndex")
        presenter.filterAndUpdateChart(muscleGroupIndex, exerciseIndex)
    }

    private fun closeHighlight() {
        chart?.highlightValues(null)
        selectedContainer?.visibility = View.INVISIBLE
        selected?.text = ""
    }
    //</editor-fold>


    companion object {
        private const val LOG_TAG = "StatisticsFragment"
        private const val X_LABELS_COUNT = 8
        private const val Y_LABELS_COUNT = 5
        private const val Y_AXIS_MINIMUM = 0
    }
}