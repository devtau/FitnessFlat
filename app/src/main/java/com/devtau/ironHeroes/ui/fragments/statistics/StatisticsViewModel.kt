package com.devtau.ironHeroes.ui.fragments.statistics

import android.util.LongSparseArray
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.devtau.ironHeroes.BaseViewModel
import com.devtau.ironHeroes.BuildConfig
import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.adapters.IronSpinnerAdapter
import com.devtau.ironHeroes.data.Result
import com.devtau.ironHeroes.data.model.*
import com.devtau.ironHeroes.data.model.wrappers.EditDialogDataWrapper
import com.devtau.ironHeroes.data.model.wrappers.EntriesWrapper
import com.devtau.ironHeroes.data.model.wrappers.StatisticsDataWrapper
import com.devtau.ironHeroes.data.source.repositories.ExercisesInTrainingsRepository
import com.devtau.ironHeroes.data.source.repositories.ExercisesRepository
import com.devtau.ironHeroes.data.source.repositories.HeroesRepository
import com.devtau.ironHeroes.data.source.repositories.MuscleGroupsRepository
import com.devtau.ironHeroes.enums.HumanType
import com.devtau.ironHeroes.ui.ResourceResolver
import com.devtau.ironHeroes.util.DateUtils
import com.devtau.ironHeroes.util.Event
import com.devtau.ironHeroes.util.prefs.PreferencesManager
import com.devtau.ironHeroes.util.print
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

class StatisticsViewModel(
    private val heroesRepository: HeroesRepository,
    private val muscleGroupsRepository: MuscleGroupsRepository,
    private val exercisesRepository: ExercisesRepository,
    private val exercisesInTrainingsRepository: ExercisesInTrainingsRepository,
    private val prefs: PreferencesManager,
    //TODO: consider moving to view
    private val resourceResolver: ResourceResolver
): BaseViewModel() {

    private val forceUpdate = MutableLiveData(false)

    val heroes: LiveData<List<Hero>> = forceUpdate.switchMap {
        heroesRepository.observeList(HumanType.HERO).switchMap {
            MutableLiveData(if (it is Result.Success) it.data else emptyList())
        }
    }
    val selectedHeroId = MutableLiveData<Long?>(null)
    val heroSelectedListener = object: IronSpinnerAdapter.ItemSelectedListener {
        override fun onItemSelected(item: SpinnerItem?, humanType: HumanType?) {
            if (selectedHeroId.value != item?.id) {
                selectedHeroId.value = item?.id
                filterAndUpdateChart()
                exercisesFiltered.value = filterExercises(
                    exercises.value, selectedMuscleGroupId.value, exercisesInTrainings.value
                )
            }
        }
    }


    val muscleGroups: LiveData<List<MuscleGroup>> = forceUpdate.switchMap {
        muscleGroupsRepository.observeList().switchMap {
            MutableLiveData(if (it is Result.Success) it.data else emptyList())
        }
    }
    val selectedMuscleGroupId = MutableLiveData<Long?>(null)
    val muscleGroupSelectedListener = object: IronSpinnerAdapter.ItemSelectedListener {
        override fun onItemSelected(item: SpinnerItem?, humanType: HumanType?) {
            if (selectedMuscleGroupId.value != item?.id) {
                selectedMuscleGroupId.value = item?.id
                filterAndUpdateChart()
                exercisesFiltered.value = filterExercises(
                    exercises.value, selectedMuscleGroupId.value, exercisesInTrainings.value
                )
            }
        }
    }


    val exercises: LiveData<List<Exercise>> = forceUpdate.switchMap {
        exercisesRepository.observeList().switchMap {
            MutableLiveData(if (it is Result.Success) it.data else emptyList())
        }
    }
    val exercisesFiltered = MutableLiveData<List<Exercise>>()
    val selectedExerciseId = MutableLiveData<Long?>(null)
    val exerciseSelectedListener = object: IronSpinnerAdapter.ItemSelectedListener {
        override fun onItemSelected(item: SpinnerItem?, humanType: HumanType?) {
            if (selectedExerciseId.value != item?.id) {
                selectedExerciseId.value = item?.id
                filterAndUpdateChart()
            }
        }
    }


    val exercisesInTrainings: LiveData<List<ExerciseInTraining>> = forceUpdate.switchMap {
        exercisesInTrainingsRepository.observeList().switchMap {
            MutableLiveData(if (it is Result.Success) it.data else emptyList())
        }
    }
    private var exercisesInTrainingsFiltered = ArrayList<ExerciseInTraining>()


    val showStatisticsData = MutableLiveData<StatisticsDataWrapper>()


    val showExerciseDetails = MutableLiveData<Event<EditDialogDataWrapper>>()
    val onBalloonClickedListener = object: OnChartBalloonClickedListener {
        override fun onBalloonClicked(trainingId: Long?, exerciseInTrainingId: Long?) {
            if (prefs.openEditDialogFromStatistics) {
                val heroId = selectedHeroId.value
                if (heroId == null || trainingId == null || exerciseInTrainingId == null) {
                    Timber.e("openExercise. bad data. aborting")
                    return
                }
                showExerciseDetails.value = Event(EditDialogDataWrapper(heroId, trainingId, exerciseInTrainingId))
            }
        }
    }

    private fun filterAndUpdateChart() {
        val muscleGroupId = selectedMuscleGroupId.value
        val exerciseId = selectedExerciseId.value
        val heroId = selectedHeroId.value
        val exercises = exercises.value
        val exercisesDone = exercisesInTrainings.value
        if (muscleGroupId == null || heroId == null || exercises == null || exercisesDone == null) return
        Timber.d("filterAndUpdateChart. muscleGroupId=$muscleGroupId, exerciseId=$exerciseId, heroId=$heroId, exercises size=${exercises.size}, exercisesDone size=${exercisesDone.size}")

        exercisesInTrainingsFiltered = filterExercisesInTrainings(exercisesDone, muscleGroupId, exerciseId, heroId)
        publishDataToChart(exercisesInTrainingsFiltered)
    }

    private fun publishDataToChart(exercisesInTrainingsFiltered: List<ExerciseInTraining>) {
        val dates = parseTrainingDates(exercisesInTrainingsFiltered)
        val dataSets = convertToDataSets(exercisesInTrainingsFiltered, dates, R.color.colorAccent)
        val xLabelsCount = if (dates.size > X_LABELS_MAX_COUNT) X_LABELS_MAX_COUNT else dates.size
        showStatisticsData.value = StatisticsDataWrapper(dataSets, dates, xLabelsCount)
    }

    private fun filterExercisesInTrainings(
        list: List<ExerciseInTraining>,
        muscleGroupId: Long?,
        exerciseId: Long?,
        heroId: Long?
    ) = list.filter {
        (muscleGroupId == null || it.exercise?.muscleGroupId == muscleGroupId)
                && (exerciseId == null || it.exercise?.id == exerciseId)
                && it.training?.heroId == heroId
    } as ArrayList

    private fun filterExercises(
        exercises: List<Exercise>?,
        muscleGroupId: Long?,
        exercisesInTrainings: List<ExerciseInTraining>?
    ): List<Exercise> {
        if (exercises == null || exercisesInTrainings == null) return emptyList()
        val listFiltered = exercises.filter {
            (muscleGroupId == null || it.muscleGroupId == muscleGroupId) && exerciseBeenUsed(exercisesInTrainings, it.id)
        }
        listFiltered.print("filterExercises")
        return listFiltered
    }

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
                Timber.e("parseTrainingDates. bad data. aborting")
                snackbarText.value = Event(R.string.parse_error)
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
            for (next in dates) datesFormatted.add(DateUtils.formatDateTimeWithWeekDay(next))
        }
        return dates
    }

    private fun checkSortOrder(list: List<ExerciseInTraining>): Boolean {
        for (i in list.indices) {
            try {
                val previous = list[i - 1].training?.getDateCal()
                val current = list[i].training?.getDateCal()
                if (previous == null || current == null) {
                    Timber.e("checkSortOrder. bad data. aborting")
                    snackbarText.value = Event(R.string.parse_error)
                    return false
                }
                if (previous.after(current)) {
                    val previousFormatted = DateUtils.formatDateTimeWithWeekDay(previous)
                    val currentFormatted = DateUtils.formatDateTimeWithWeekDay(current)
                    Timber.e("checkSortOrder. list not sorted because $previousFormatted > $currentFormatted")
                    snackbarText.value = Event(R.string.parse_error)
                    return false
                }
            } catch (e: IndexOutOfBoundsException) { }
        }
        return true
    }

    private fun convertToDataSets(
        exercises: List<ExerciseInTraining>?,
        dates: List<Calendar>?,
        markerColorId: Int
    ): LineData {
        val valuesMap = LongSparseArray<ArrayList<Entry>>()
        if (exercises != null) for (next in exercises) valuesMap.put(next.exerciseId!!, arrayListOf())
        val dataSets = ArrayList<ILineDataSet>()

        for (i in 0 until valuesMap.size()) {
            val lineWrapper = parseLine(valuesMap.keyAt(i), exercises, dates, getLineColor(i))
            val lineColor = resourceResolver.resolveColor(getLineColor(i))
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
        line.fillColor = resourceResolver.resolveColor(R.color.colorAccent)
        line.fillAlpha = 100
        return line
    }


    companion object {
        private const val X_LABELS_MAX_COUNT = 8
    }
}