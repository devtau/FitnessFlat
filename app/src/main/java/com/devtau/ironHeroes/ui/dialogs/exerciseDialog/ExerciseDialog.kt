package com.devtau.ironHeroes.ui.dialogs.exerciseDialog

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import android.view.*
import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.data.model.ExerciseInTraining
import com.devtau.ironHeroes.ui.DependencyRegistry
import com.devtau.ironHeroes.ui.dialogs.ViewSubscriberDialog
import com.devtau.ironHeroes.util.AppUtils
import com.devtau.ironHeroes.util.Constants.EXERCISE_IN_TRAINING_ID
import com.devtau.ironHeroes.util.Constants.TRAINING_ID
import com.devtau.ironHeroes.util.Logger
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.dialog_exercise.*

class ExerciseDialog: ViewSubscriberDialog(),
    ExerciseView {

    lateinit var presenter: ExercisePresenter
    private var listener: Listener? = null


    //<editor-fold desc="Framework overrides">
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Listener) listener = context
        else throw RuntimeException("$context must implement $LOG_TAG Listener")
    }

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
    override fun showMsg(msgId: Int, confirmedListener: Action?) = showMsg(getString(msgId), confirmedListener)
    override fun showMsg(msg: String, confirmedListener: Action?){
        if (context != null) AppUtils.alertD(LOG_TAG, msg, context!!, confirmedListener)
    }

    override fun showMuscleGroups(list: List<String>?, selectedIndex: Int) =
        AppUtils.initSpinner(muscleGroup, list, selectedIndex, context)

    override fun showExercises(list: List<String>?, selectedIndex: Int) =
        AppUtils.initSpinner(exercise, list, selectedIndex, context)

    override fun showExerciseDetails(exercise: ExerciseInTraining?) {
        AppUtils.updateInputField(weightInput, exercise?.weight?.toString())
        AppUtils.updateInputField(countInput, exercise?.count?.toString() ?: ExerciseInTraining.DEFAULT_COUNT)
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
            countInput?.text?.toString())
    }

    private fun applyFilter() = presenter.filterAndUpdateList(muscleGroup?.selectedItemPosition ?: 0)
    //</editor-fold>


    interface Listener {
    }


    companion object {
        private const val LOG_TAG = "ExerciseDialog"
        private const val FRAGMENT_TAG = "com.devtau.ironHeroes.ui.dialogs.exerciseDialog.ExerciseDialog"

        fun showDialog(fragmentManager: FragmentManager?, trainingId: Long?, exerciseInTrainingId: Long?, listener: Listener) {
            if (fragmentManager == null || trainingId == null) {
                Logger.e(LOG_TAG, "showDialog. bad data. aborting")
                return
            }
            val ft = fragmentManager.beginTransaction()
            val prev = fragmentManager.findFragmentByTag(FRAGMENT_TAG)
            if (prev != null) ft.remove(prev)
            ft.addToBackStack(null)
            val newFragment = newInstance(trainingId, exerciseInTrainingId)
            newFragment.show(ft, FRAGMENT_TAG)
        }

        private fun newInstance(trainingId: Long?, exerciseInTrainingId: Long?): ExerciseDialog {
            val fragment = ExerciseDialog()
            val args = Bundle()
            if (trainingId != null) args.putLong(TRAINING_ID, trainingId)
            if (exerciseInTrainingId != null) args.putLong(EXERCISE_IN_TRAINING_ID, exerciseInTrainingId)
            fragment.arguments = args
            return fragment
        }
    }
}