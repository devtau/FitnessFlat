package com.devtau.ironHeroes.ui.fragments.statistics

import android.util.LongSparseArray
import com.devtau.ironHeroes.BuildConfig
import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.data.dao.ExerciseDao
import com.devtau.ironHeroes.data.dao.ExerciseInTrainingDao
import com.devtau.ironHeroes.data.dao.MuscleGroupDao
import com.devtau.ironHeroes.data.model.Exercise
import com.devtau.ironHeroes.data.model.ExerciseInTraining
import com.devtau.ironHeroes.data.model.MuscleGroup
import com.devtau.ironHeroes.data.relations.ExerciseInTrainingRelation
import com.devtau.ironHeroes.data.relations.ExerciseRelation
import com.devtau.ironHeroes.data.subscribeDefault
import com.devtau.ironHeroes.ui.DBSubscriber
import com.devtau.ironHeroes.util.AppUtils
import com.devtau.ironHeroes.util.EntriesWrapper
import com.devtau.ironHeroes.util.Logger
import com.devtau.ironHeroes.util.PreferencesManager
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import io.reactivex.functions.Consumer
import java.util.*
import kotlin.collections.ArrayList

class StatisticsPresenterImpl(
    private val view: StatisticsContract.View,
    private val exerciseDao: ExerciseDao,
    private val exerciseInTrainingDao: ExerciseInTrainingDao,
    private val muscleGroupDao: MuscleGroupDao,
    private val prefs: PreferencesManager,
    private val heroId: Long
): DBSubscriber(), StatisticsContract.Presenter {

    private var muscleGroups = ArrayList<MuscleGroup>()
    private var exercises = ArrayList<Exercise>()
    private var exercisesFiltered = ArrayList<Exercise>()
    private var exercisesInTrainings = ArrayList<ExerciseInTraining>()
    private var exercisesInTrainingsFiltered = ArrayList<ExerciseInTraining>()


    //<editor-fold desc="Presenter overrides">
    override fun restartLoaders() {
        disposeOnStop(muscleGroupDao.getList()
            .subscribeDefault(Consumer {
                muscleGroups.clear()
                muscleGroups.addAll(it)
                prepareAndPublishDataToView()
            }, "muscleGroupDao.getList"))

        disposeOnStop(exerciseDao.getList()
            .map { relation -> ExerciseRelation.convertList(relation) }
            .subscribeDefault(Consumer {
                exercises.clear()
                exercises.addAll(it)
                prepareAndPublishDataToView()
            }, "exerciseDao.getList"))

        disposeOnStop(exerciseInTrainingDao.getListForHeroAsc(heroId)
            .map { relation -> ExerciseInTrainingRelation.convertList(relation) }
            .subscribeDefault(Consumer {
                exercisesInTrainings.clear()
                exercisesInTrainings.addAll(it)
                prepareAndPublishDataToView()
            }, "exerciseInTrainingDao.getListForHeroAsc"))
    }

    override fun filterAndUpdateChart(muscleGroupIndex: Int, exerciseIndex: Int) {
        val muscleGroupId = muscleGroups[muscleGroupIndex].id
        exercisesInTrainingsFiltered = filterExercisesInTrainings(exercisesInTrainings, muscleGroupId)
        val dates = parseTrainingDates(exercisesInTrainingsFiltered)
        val dataSets = convertToDataSets(exercisesInTrainingsFiltered, dates, R.color.colorAccent)
        val xLabelsCount = if (dates.size > X_LABELS_MAX_COUNT) X_LABELS_MAX_COUNT else dates.size
        view.showStatisticsData(dataSets, dates, xLabelsCount)
        exercisesFiltered = filterExercises(exercises, muscleGroupId)
        view.showExercises(AppUtils.getExercisesSpinnerStrings(exercisesFiltered, true), 0)
    }

    override fun onBalloonClicked(trainingId: Long?, exerciseInTrainingId: Long?) {
        if (prefs.openEditDialogFromStatistics)
            view.showExerciseDetails(heroId, trainingId, exerciseInTrainingId)
    }
    //</editor-fold>


    //<editor-fold desc="Private methods">
    private fun prepareAndPublishDataToView() {
        if (AppUtils.isEmpty(muscleGroups) || AppUtils.isEmpty(exercises) || AppUtils.isEmpty(exercisesInTrainings)) return

        view.showMuscleGroups(AppUtils.getMuscleGroupsSpinnerStrings(muscleGroups), 0)
        view.showExercises(AppUtils.getExercisesSpinnerStrings(exercises, true), 0)
        filterAndUpdateChart(0, 0)
    }

    private fun filterExercisesInTrainings(list: List<ExerciseInTraining>?, muscleGroupId: Long?): ArrayList<ExerciseInTraining> {
        val filtered = ArrayList<ExerciseInTraining>()
        if (list != null) for (next in list)
            if (muscleGroupId == null || next.exercise?.muscleGroupId == muscleGroupId) filtered.add(next)
        Logger.d(LOG_TAG, "filterExercisesInTrainings. list size=${list?.size}, muscleGroupId=$muscleGroupId, filtered size=${filtered.size}")
        return filtered
    }

    private fun filterExercises(list: List<Exercise>?, muscleGroupId: Long?): ArrayList<Exercise> {
        val filtered = ArrayList<Exercise>()
        if (list != null) for (next in list)
            if (muscleGroupId == null || next.muscleGroupId == muscleGroupId) filtered.add(next)
        Logger.d(LOG_TAG, "filterExercises. list size=${list?.size}, muscleGroupId=$muscleGroupId, filtered size=${filtered.size}")
        return filtered
    }

    private fun parseLine(exerciseId: Long?, exercises: List<ExerciseInTraining>?, dates: List<Calendar>?,
                          markerColorId: Int): EntriesWrapper {
        val values = ArrayList<Entry>()
        var label = ""
        if (exercises != null && exercises.isNotEmpty()) {
            for (next in exercises) {
                val date = next.training?.date
                if (next.exerciseId == exerciseId && date != null) {
                    val tag = Tag(markerColorId, Tag.getTitle(next), next.trainingId, next.training?.date, next.id)
                    values.add(Entry(getDateIndex(date, dates), next.calculateWork().toFloat(), tag))
                    label = next.exercise?.name ?: ""
                }
            }
        }
        return EntriesWrapper(values, label)
    }

    private fun getDateIndex(trainingDate: Long?, list: List<Calendar>?): Float {
        if (trainingDate != null && list != null && list.isNotEmpty()) {
            for ((i, dateStart) in list.withIndex()) {
                val dateEnd = Calendar.getInstance()
                dateEnd.timeInMillis = dateStart.timeInMillis
                dateEnd.add(Calendar.DAY_OF_MONTH, 1)

                if (dateStart.timeInMillis <= trainingDate && trainingDate < dateEnd.timeInMillis) return i.toFloat()
            }
        }
        return 0f
    }

    private fun parseTrainingDates(list: List<ExerciseInTraining>?): List<Calendar> {
        val dates = ArrayList<Calendar>()
        if (list != null && list.isNotEmpty() && checkSortOrder(list)) {
            if (list.size > 1 && !checkSortOrder(list)) return dates
            val firstDateStart = list[0].training?.getDateCal()
            val lastDateEnd = (list[if (list.size == 1) 0 else list.size - 1]).training?.getDateCal()
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
        if (exercises != null) for (next in exercises) valuesMap.put(next.exerciseId!!, arrayListOf())
        val dataSets = ArrayList<ILineDataSet>()

        for (i in 0 until valuesMap.size()) {
            val lineWrapper = parseLine(valuesMap.keyAt(i), exercises, dates, getLineColor(i))
            val lineColor = view.resolveColor(getLineColor(i))
            dataSets.add(formatLine(LineDataSet(lineWrapper.entries, lineWrapper.label), lineColor))
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
        private const val X_LABELS_MAX_COUNT = 8
    }
}