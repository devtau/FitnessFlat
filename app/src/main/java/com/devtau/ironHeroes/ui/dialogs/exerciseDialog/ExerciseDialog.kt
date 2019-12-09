package com.devtau.ironHeroes.ui.dialogs.exerciseDialog

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import android.view.*
import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.data.model.ExerciseInTraining
import com.devtau.ironHeroes.ui.DependencyRegistry
import com.devtau.ironHeroes.ui.dialogs.ViewSubscriberDialog
import com.devtau.ironHeroes.util.AppUtils
import com.devtau.ironHeroes.util.Constants.EXERCISE_IN_TRAINING_ID
import com.devtau.ironHeroes.util.Constants.HERO_ID
import com.devtau.ironHeroes.util.Constants.POSITION
import com.devtau.ironHeroes.util.Constants.TRAINING_ID
import com.devtau.ironHeroes.util.Logger
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.dialog_exercise.*

class ExerciseDialog: ViewSubscriberDialog(),
    ExerciseView {

    lateinit var presenter: ExercisePresenter


    //<editor-fold desc="Framework overrides">
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DependencyRegistry().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return inflater.inflate(R.layout.dialog_exercise, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    override fun onStart() {
        super.onStart()
        presenter.restartLoaders()
        subscribeField(muscleGroup, Consumer { applyFilter() })
        subscribeField(exercise, Consumer { presenter.updatePreviousExerciseData(exercise?.selectedItemPosition ?: 0) })
    }

    override fun onResume() {
        super.onResume()
        val window = dialog?.window ?: return
        val params = window.attributes
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        window.attributes = params as WindowManager.LayoutParams
    }

    override fun onStop() {
        super.onStop()
        presenter.onStop()
    }
    //</editor-fold>


    //<editor-fold desc="View overrides">
    override fun getLogTag() = LOG_TAG
    override fun showMuscleGroups(list: List<String>?, selectedIndex: Int) =
        AppUtils.initSpinner(muscleGroup, list, selectedIndex, context)

    override fun showExercises(list: List<String>?, selectedIndex: Int) =
        AppUtils.initSpinner(exercise, list, selectedIndex, context)

    override fun showExerciseDetails(weight: Int?, repeats: Int?, count: Int?, comment: String?) {
        AppUtils.updateInputField(weightInput, weight?.toString())
        AppUtils.updateInputField(repeatsInput, repeats?.toString() ?: ExerciseInTraining.DEFAULT_REPEATS)
        AppUtils.updateInputField(countInput, count?.toString() ?: ExerciseInTraining.DEFAULT_COUNT)
        AppUtils.updateInputField(commentInput, comment)
    }

    override fun showPreviousExerciseData(date: Long?, weight: Int?, repeats: Int?, count: Int?) {
        AppUtils.updateInputField(previousExerciseData, composePreviousExerciseDataString(date, weight, repeats, count))
    }
    //</editor-fold>


    //<editor-fold desc="Private methods">
    private fun initUi() {
        cancel.setOnClickListener { dialog?.dismiss() }
        delete.setOnClickListener {
            presenter.deleteExercise()
            dialog?.dismiss()
        }
        save.setOnClickListener {
            updateExerciseData()
            dialog?.dismiss()
        }
    }

    private fun updateExerciseData() {
        val exerciseIndex = exercise?.selectedItemPosition
        if (exerciseIndex != null) presenter.updateExerciseData(
            exerciseIndex,
            weightInput?.text?.toString(),
            repeatsInput?.text?.toString(),
            countInput?.text?.toString(),
            commentInput?.text?.toString())
    }

    private fun applyFilter() = presenter.filterAndUpdateList(muscleGroup?.selectedItemPosition ?: 0)

    private fun composePreviousExerciseDataString(date: Long?, weight: Int?, repeats: Int?, count: Int?) =
        if (date == null || weight == null || repeats == null || count == null) {
            context?.getString(R.string.no_data)
        } else {
            val formatter = context?.getString(R.string.previous_training_data_formatter) ?: ""
            val dateFormatted = AppUtils.formatDateWithWeekDay(date)
            String.format(formatter, dateFormatted, weight.toString(), repeats.toString(), count.toString())
        }
    //</editor-fold>


    companion object {
        private const val LOG_TAG = "ExerciseDialog"
        private const val FRAGMENT_TAG = "com.devtau.ironHeroes.ui.dialogs.exerciseDialog.ExerciseDialog"

        fun showDialog(fragmentManager: FragmentManager?, heroId: Long?, trainingId: Long?,
                       exerciseInTrainingId: Long?, position: Int? = null) {
            if (fragmentManager == null || heroId == null || trainingId == null) {
                Logger.e(LOG_TAG, "showDialog. bad data. aborting")
                return
            }
            val ft = fragmentManager.beginTransaction()
            val prev = fragmentManager.findFragmentByTag(FRAGMENT_TAG)
            if (prev != null) ft.remove(prev)
            ft.addToBackStack(null)
            val newFragment = newInstance(heroId, trainingId, exerciseInTrainingId, position)
            newFragment.show(ft, FRAGMENT_TAG)
        }

        private fun newInstance(heroId: Long, trainingId: Long?, exerciseInTrainingId: Long?, position: Int?): ExerciseDialog {
            val fragment = ExerciseDialog()
            val args = Bundle()
            args.putLong(HERO_ID, heroId)
            if (trainingId != null) args.putLong(TRAINING_ID, trainingId)
            if (exerciseInTrainingId != null) args.putLong(EXERCISE_IN_TRAINING_ID, exerciseInTrainingId)
            if (position != null) args.putInt(POSITION, position)
            fragment.arguments = args
            return fragment
        }
    }
}