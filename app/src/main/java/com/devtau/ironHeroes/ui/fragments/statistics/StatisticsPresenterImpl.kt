package com.devtau.ironHeroes.ui.fragments.statistics

import android.util.LongSparseArray
import com.devtau.ironHeroes.BuildConfig
import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.data.dao.ExerciseDao
import com.devtau.ironHeroes.data.dao.ExerciseInTrainingDao
import com.devtau.ironHeroes.data.dao.HeroDao
import com.devtau.ironHeroes.data.dao.MuscleGroupDao
import com.devtau.ironHeroes.data.model.Exercise
import com.devtau.ironHeroes.data.model.ExerciseInTraining
import com.devtau.ironHeroes.data.model.Hero
import com.devtau.ironHeroes.data.model.MuscleGroup
import com.devtau.ironHeroes.data.subscribeDefault
import com.devtau.ironHeroes.enums.HumanType
import com.devtau.ironHeroes.ui.DBSubscriber
import com.devtau.ironHeroes.util.*
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import io.reactivex.functions.Consumer
import java.util.*
import kotlin.collections.ArrayList

class StatisticsPresenterImpl(
    private val view: StatisticsContract.View,
    private val heroDao: HeroDao,
    private val exerciseDao: ExerciseDao,
    private val exerciseInTrainingDao: ExerciseInTrainingDao,
    private val muscleGroupDao: MuscleGroupDao,
    private val prefs: PreferencesManager
): DBSubscriber(), StatisticsContract.Presenter {

    private var heroes = ArrayList<Hero>()
    private var muscleGroups = ArrayList<MuscleGroup>()
    private var exercises = ArrayList<Exercise>()
    private var exercisesFiltered = ArrayList<Exercise>()
    private var exercisesInTrainings = ArrayList<ExerciseInTraining>()
    private var exercisesInTrainingsFiltered = ArrayList<ExerciseInTraining>()
    private var heroId: Long? = 3L


    //<editor-fold desc="Interface overrides">
    override fun restartLoaders() {
        disposeOnStop(heroDao.getList(HumanType.HERO.ordinal)
            .subscribeDefault(Consumer {
                Logger.d(LOG_TAG, "got new heroes list with size=${it.size}")
                heroes.clear()
                heroes.addAll(it)
                prepareAndPublishDataToView()
            }, "heroDao.getList"))

        disposeOnStop(muscleGroupDao.getList()
            .subscribeDefault(Consumer {
                Logger.d(LOG_TAG, "got new muscleGroups list with size=${it.size}")
                muscleGroups.clear()
                muscleGroups.addAll(it)
                prepareAndPublishDataToView()
            }, "muscleGroupDao.getList"))

        disposeOnStop(exerciseDao.getList()
            .map { relation -> relation.map { it.convert() } }
            .subscribeDefault(Consumer {
                Logger.d(LOG_TAG, "got new exercises list with size=${it.size}")
                exercises.clear()
                exercises.addAll(it)
                prepareAndPublishDataToView()
            }, "exerciseDao.getList"))

        disposeOnStop(exerciseInTrainingDao.getList()
            .map { relation -> relation.map { it.convert() } }
            .subscribeDefault(Consumer {
                Logger.d(LOG_TAG, "got new exercisesInTrainings list with size=${it.size}")
                exercisesInTrainings.clear()
                exercisesInTrainings.addAll(it)
                prepareAndPublishDataToView()
            }, "exerciseInTrainingDao.getListForHeroAsc"))
    }

    override fun filterAndUpdateChart(muscleGroupIndex: Int, exerciseIndex: Int, heroIndex: Int) {
        if (!muscleGroups.inBounds(muscleGroupIndex) || !exercisesInTrainings.inBounds(exerciseIndex) || !heroes.inBounds(heroIndex)) {
            Logger.w(LOG_TAG, "filterAndUpdateChart. data lists are not ready. aborting")
            return
        }
        val muscleGroupId = muscleGroups[muscleGroupIndex].id
        heroId = heroes[heroIndex].id
        exercisesInTrainingsFiltered = filterExercisesInTrainings(exercisesInTrainings, muscleGroupId, heroId)
        val dates = parseTrainingDates(exercisesInTrainingsFiltered)
        val dataSets = convertToDataSets(exercisesInTrainingsFiltered, dates, R.color.colorAccent)
        val xLabelsCount = if (dates.size > X_LABELS_MAX_COUNT) X_LABELS_MAX_COUNT else dates.size
        view.showStatisticsData(dataSets, dates, xLabelsCount)

        exercisesFiltered = filterExercises(exercises, muscleGroupId, exercisesInTrainingsFiltered)
        view.showExercises(SpinnerUtils.getExercisesSpinnerStrings(exercisesFiltered, true), exerciseIndex)
    }

    override fun onBalloonClicked(trainingId: Long?, exerciseInTrainingId: Long?) {
        if (prefs.openEditDialogFromStatistics)
            view.showExerciseDetails(heroId, trainingId, exerciseInTrainingId)
    }
    //</editor-fold>


    //<editor-fold desc="Private methods">
    private fun prepareAndPublishDataToView() {
        if (heroes.isEmpty() || muscleGroups.isEmpty() || exercises.isEmpty() || exercisesInTrainings.isEmpty()) return

        view.showHeroes(SpinnerUtils.getHeroesSpinnerStrings(heroes), 0)
        view.showMuscleGroups(SpinnerUtils.getMuscleGroupsSpinnerStrings(muscleGroups), 0)
        view.showExercises(SpinnerUtils.getExercisesSpinnerStrings(exercises, true), 0)
        filterAndUpdateChart(0, 0, 0)
    }

    private fun filterExercisesInTrainings(
        list: List<ExerciseInTraining>,
        muscleGroupId: Long?,
        heroId: Long?
    ) = list.filter {
        (muscleGroupId == null || it.exercise?.muscleGroupId == muscleGroupId) && it.training?.heroId == heroId
    } as ArrayList

    private fun filterExercises(
        exercises: List<Exercise>,
        muscleGroupId: Long?,
        exercisesInTrainings: List<ExerciseInTraining>
    ) = exercises.filter {
        (muscleGroupId == null || it.muscleGroupId == muscleGroupId) && exerciseBeenUsed(exercisesInTrainings, it.id)
    } as ArrayList

    private fun exerciseBeenUsed(exercisesInTrainings: List<ExerciseInTraining>, exerciseId: Long?): Boolean =
        exercisesInTrainings.any { it.exerciseId == exerciseId }

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