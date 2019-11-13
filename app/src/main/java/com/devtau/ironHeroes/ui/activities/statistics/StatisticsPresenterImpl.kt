package com.devtau.ironHeroes.ui.activities.statistics

import android.util.LongSparseArray
import com.devtau.ironHeroes.BuildConfig
import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.data.DataLayer
import com.devtau.ironHeroes.data.model.Exercise
import com.devtau.ironHeroes.data.model.ExerciseInTraining
import com.devtau.ironHeroes.data.model.MuscleGroup
import com.devtau.ironHeroes.rest.NetworkLayer
import com.devtau.ironHeroes.ui.DBSubscriber
import com.devtau.ironHeroes.util.AppUtils
import com.devtau.ironHeroes.util.Logger
import com.devtau.ironHeroes.util.PreferencesManager
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import io.reactivex.functions.Consumer
import java.lang.IndexOutOfBoundsException
import java.util.*

class StatisticsPresenterImpl(
    private val view: StatisticsView,
    private val dataLayer: DataLayer,
    private val networkLayer: NetworkLayer,
    private val prefs: PreferencesManager,
    private val heroId: Long
): DBSubscriber(), StatisticsPresenter {

    private var muscleGroups: List<MuscleGroup>? = null
    private var exercises: List<Exercise>? = null
    private var exercisesInTrainings: List<ExerciseInTraining>? = null
    private var exercisesFiltered: List<ExerciseInTraining>? = null


    //<editor-fold desc="Presenter overrides">
    override fun restartLoaders() {
        disposeOnStop(dataLayer.getMuscleGroups(Consumer {
            muscleGroups = it
            prepareAndPublishDataToView()
        }))
        disposeOnStop(dataLayer.getExercises(Consumer {
            exercises = it
            prepareAndPublishDataToView()
        }))
        dataLayer.getAllExercisesInTrainingsAndClose(heroId, Calendar.getInstance().timeInMillis, true, Consumer {
            exercisesInTrainings = it
            prepareAndPublishDataToView()
        })
    }

    override fun filterAndUpdateChart(muscleGroupIndex: Int, exerciseIndex: Int) {
        val muscleGroupId = muscleGroups?.get(muscleGroupIndex)?.id
        exercisesFiltered = filter(exercisesInTrainings, muscleGroupId)
        view.showStatisticsData(convertToDataSets(exercisesFiltered, parseTrainingDates(exercisesFiltered), R.color.colorAccent))
//        view.showStatisticsData(generateMockDataSets(20, 50, 150))
    }
    //</editor-fold>


    //<editor-fold desc="Private methods">
    private fun prepareAndPublishDataToView() {
        if (AppUtils.isEmpty(muscleGroups) || AppUtils.isEmpty(exercises) || AppUtils.isEmpty(exercisesInTrainings)) return

        view.showMuscleGroups(AppUtils.getMuscleGroupsSpinnerStrings(muscleGroups), 0)
        view.showExercises(AppUtils.getExercisesSpinnerStrings(exercises), 0)
        filterAndUpdateChart(0, 0)
    }

    private fun filter(list: List<ExerciseInTraining>?, muscleGroupId: Long?): List<ExerciseInTraining>? {
        val filtered = ArrayList<ExerciseInTraining>()
        if (list != null) for (next in list)
            if (muscleGroupId == null || next.exercise?.muscleGroupId == muscleGroupId) filtered.add(next)
        Logger.d(LOG_TAG, "filter. list size=${list?.size}, muscleGroupId=$muscleGroupId, filtered size=${filtered.size}")
        return filtered
    }

    private fun parseLine(exerciseId: Long?, exercises: List<ExerciseInTraining>?, dates: List<Calendar>?,
                          markerColorId: Int): ArrayList<Entry> {
        val values = ArrayList<Entry>()
        if (exercises != null && exercises.isNotEmpty()) {
            for (next in exercises) {
                if (next.exerciseId == exerciseId) {
                    val dateIndex = getDateIndex(next.training?.date, dates)
                    if (dateIndex == null) {
                        view.showMsg("parseLine. bad data. aborting")
                        return values
                    }
                    values.add(Entry(dateIndex.toFloat(), next.calculateWork().toFloat(), markerColorId))
                }
            }
        }
        return values
    }

    private fun getDateIndex(trainingDate: Long?, list: List<Calendar>?): Int? {
        if (trainingDate != null && list != null && list.isNotEmpty()) {
            for (i in list.indices) {
                val dateStart = list[i]
                val dateEnd = Calendar.getInstance()
                dateEnd.timeInMillis = dateStart.timeInMillis
                dateEnd.add(Calendar.DAY_OF_MONTH, 1)

                if (dateStart.timeInMillis <= trainingDate && trainingDate < dateEnd.timeInMillis) return i
            }
        }
        return null
    }

    private fun parseTrainingDates(list: List<ExerciseInTraining>?): List<Calendar> {
        val dates = ArrayList<Calendar>()
        if (list != null && list.size > 1 && checkSortOrder(list)) {
            val firstDateStart = list[0].training?.getDateCal()
            val lastDateEnd = list[list.size - 1].training?.getDateCal()
            if (firstDateStart == null || lastDateEnd == null) {
                view.showMsg("parseDates. bad data. aborting")
                return dates
            }
            firstDateStart.set(Calendar.HOUR_OF_DAY, 0)
            firstDateStart.set(Calendar.MINUTE, 0)
            lastDateEnd.set(Calendar.HOUR_OF_DAY, 23)
            lastDateEnd.set(Calendar.MINUTE, 59)

            dates.add(firstDateStart)
            var i = 0
            while (true) {
                val next = Calendar.getInstance()
                next.timeInMillis = dates[i].timeInMillis
                next.add(Calendar.DAY_OF_MONTH, 1)
                if (next.before(lastDateEnd)) {
                    dates.add(next)
                    i++
                } else break
            }
        }
        val datesFormatted = ArrayList<String>()
        if (BuildConfig.DEBUG) {
            for (next in dates) datesFormatted.add(AppUtils.formatDateTimeWithWeekDay(next))
        }
        return dates
    }

    private fun checkSortOrder(list: List<ExerciseInTraining>): Boolean {
        for (i in list.indices) {
            try {
                val previous = list[i - 1].training?.getDateCal()
                val current = list[i].training?.getDateCal()
                if (previous == null || current == null) {
                    view.showMsg("checkSortOrder. bad data. aborting")
                    return false
                }
                if (previous.after(current)) {
                    val previousFormatted = AppUtils.formatDateTimeWithWeekDay(previous)
                    val currentFormatted = AppUtils.formatDateTimeWithWeekDay(current)
                    view.showMsg("checkSortOrder. list not sorted because $previousFormatted > $currentFormatted")
                    return false
                }
            } catch (e: IndexOutOfBoundsException) { }
        }
        return true
    }

    private fun convertToDataSets(exercises: List<ExerciseInTraining>?, dates: List<Calendar>?, markerColorId: Int): LineData? {
        val valuesMap = LongSparseArray<ArrayList<Entry>>()
        if (exercises != null && exercises.isNotEmpty()) for (next in exercises) {
            if (valuesMap[next.exerciseId!!] == null) valuesMap.put(next.exerciseId!!, arrayListOf())
        }
        val dataSets = ArrayList<ILineDataSet>()

        for (i in 0 until valuesMap.size()) {
            val line = parseLine(valuesMap.keyAt(i), exercises, dates, markerColorId)
            val lineColor = view.resolveColor(getLineColor(i))
            dataSets.add(formatLine(LineDataSet(line, ""), lineColor))
        }

        val data = LineData(dataSets)
        data.isHighlightEnabled = true
        return data
    }

    private fun getLineColor(index: Int): Int = when (index) {
        0 -> R.color.lineColor0
        1 -> R.color.lineColor1
        2 -> R.color.lineColor2
        3 -> R.color.lineColor3
        4 -> R.color.lineColor4
        5 -> R.color.lineColor5
        6 -> R.color.lineColor6
        7 -> R.color.lineColor7
        8 -> R.color.lineColor8
        9 -> R.color.lineColor9
        else -> R.color.lineColor1
    }

    private fun generateMockDataSets(xCount: Int, rangeMin: Int, rangeMax: Int): LineData? {
        val dataSets = ArrayList<ILineDataSet>()

        val lineColor = R.color.colorAccent
        val lineDataSet = generateMockChartData(view.resolveString(R.string.trainings), lineColor,
            xCount, rangeMin, rangeMax)
        dataSets.add(formatLine(lineDataSet, view.resolveColor(lineColor)))

        val data = LineData(dataSets)
        data.isHighlightEnabled = true
        return data
    }

    private fun generateMockChartData(label: String, markerColorId: Int, xCount: Int,
                                      rangeMin: Int, rangeMax: Int): LineDataSet {
        val values = ArrayList<Entry>()
        val random = Random()
        for (i in 1..xCount) {
            val value = random.nextInt(rangeMax - rangeMin) + rangeMin
            values.add(Entry(i.toFloat(), value.toFloat(), markerColorId))
        }
        return LineDataSet(values, label)
    }

    private fun formatLine(line: LineDataSet, lineColor: Int): LineDataSet {
        line.mode = LineDataSet.Mode.LINEAR
        line.cubicIntensity = 0.1f
        line.lineWidth = 1f
        line.color = lineColor

        line.setDrawHorizontalHighlightIndicator(false)
        line.setDrawVerticalHighlightIndicator(false)
        line.highLightColor = lineColor

        line.setDrawCircles(true)
        line.setDrawCircleHole(false)
        line.circleRadius = 3f
        line.setCircleColor(lineColor)
        line.setDrawValues(false)
        line.valueTextColor = lineColor

        line.setDrawFilled(false)
        line.fillColor = view.resolveColor(R.color.colorAccent)
        line.fillAlpha = 100
        return line
    }
    //</editor-fold>


    companion object {
        private const val LOG_TAG = "StatisticsPresenterImpl"
    }
}