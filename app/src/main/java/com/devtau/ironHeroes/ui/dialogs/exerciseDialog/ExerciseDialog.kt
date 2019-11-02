package com.devtau.ironHeroes.ui.dialogs.exerciseDialog

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.data.model.Exercise
import com.devtau.ironHeroes.data.model.ExerciseInTraining
import com.devtau.ironHeroes.ui.DependencyRegistry
import com.devtau.ironHeroes.ui.dialogs.ViewSubscriberDialog
import com.devtau.ironHeroes.util.AppUtils
import com.devtau.ironHeroes.util.Constants.EXERCISE_IN_TRAINING_ID
import com.devtau.ironHeroes.util.Constants.TRAINING_ID
import com.devtau.ironHeroes.util.Logger
import io.reactivex.functions.Action
import kotlinx.android.synthetic.main.dialog_exercise.*
import java.util.ArrayList

class ExerciseDialog: ViewSubscriberDialog(),
    ExerciseView {

    lateinit var presenter: ExercisePresenter
    private var exercises: List<Exercise>? = null
    private var exerciseInTraining: ExerciseInTraining? = null
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
//        subscribeField(exercise, Consumer { presenter.updateExercise(exerciseInTraining) })
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

    override fun showExerciseDetails(exerciseInTraining: ExerciseInTraining?, exercises: List<Exercise>?) {
        this.exercises = exercises
        this.exerciseInTraining = exerciseInTraining
        weightInput?.setText(exerciseInTraining?.weight?.toString())
        countInput?.setText(exerciseInTraining?.count?.toString())
        initSpinner(exercise, exercises, exerciseInTraining?.exerciseId)
    }
    //</editor-fold>


    //<editor-fold desc="Private methods">
    private fun initUi() {
        cancel.setOnClickListener { dialog?.dismiss() }
        save.setOnClickListener {
            dialog?.dismiss()
            presenter.updateExercise(
                exercises?.get(exercise.selectedItemPosition)?.id,
                weightInput?.text?.toString(),
                countInput?.text?.toString())
        }
    }

    private fun initSpinner(spinner: Spinner?, list: List<Exercise>?, selectedId: Long?) {
        val context = context
        if (context == null || spinner == null || list == null) {
            Logger.e(LOG_TAG, "initSpinner. bad data. aborting")
            return
        }

        val spinnerStrings = ArrayList<String>()
        var selectedItemIndex = 0
        for (i in list.indices) {
            val next = list[i]
            spinnerStrings.add(next.name)
            if (next.id == selectedId) selectedItemIndex = i
        }

        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, spinnerStrings)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.setSelection(selectedItemIndex)
    }
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