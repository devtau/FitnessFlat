package com.devtau.ironHeroes.ui.activities.trainingDetails

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.adapters.CustomLinearLayoutManager
import com.devtau.ironHeroes.adapters.ExercisesInTrainingAdapter
import com.devtau.ironHeroes.data.model.ExerciseInTraining
import com.devtau.ironHeroes.ui.DependencyRegistry
import com.devtau.ironHeroes.ui.activities.ViewSubscriberActivity
import com.devtau.ironHeroes.ui.dialogs.exerciseDialog.ExerciseDialog
import com.devtau.ironHeroes.util.AppUtils
import com.devtau.ironHeroes.util.Constants.TRAINING_ID
import com.devtau.ironHeroes.util.Logger
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.activity_training_details.*
import java.util.*

class TrainingDetailsActivity: ViewSubscriberActivity(),
    TrainingDetailsView, ExerciseDialog.Listener {

    lateinit var presenter: TrainingDetailsPresenter
    private var exercisesAdapter: ExercisesInTrainingAdapter? = null
    private var deleteTrainingBtn: MenuItem? = null
    private var deleteTrainingBtnVisibility: Boolean = false
    private var trainingDate: Calendar? = null


    //<editor-fold desc="Framework overrides">
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_training_details)
        DependencyRegistry().inject(this)
        initUi()
        initList()
    }

    override fun onStart() {
        super.onStart()
        presenter.restartLoaders()
        subscribeField(champion, Consumer { updateTrainingData() })
        subscribeField(hero, Consumer { updateTrainingData() })
    }

    override fun onStop() {
        super.onStop()
        presenter.onStop()
    }

    override fun onBackPressed() {
        presenter.onBackPressed(Action { super.onBackPressed() })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_training_details, menu)
        deleteTrainingBtn = menu.getItem(0)
        deleteTrainingBtn?.isVisible = deleteTrainingBtnVisibility
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.delete -> {
            presenter.deleteTraining()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
    //</editor-fold>


    //<editor-fold desc="View overrides">
    override fun showMsg(msgId: Int, confirmedListener: Action?) = showMsg(getString(msgId), confirmedListener)
    override fun showMsg(msg: String, confirmedListener: Action?) = AppUtils.alertD(LOG_TAG, msg, this, confirmedListener)

    override fun showScreenTitle(newTraining: Boolean) {
        val toolbarTitle = if (newTraining) R.string.training_add else R.string.training_edit
        AppUtils.initToolbar(this, toolbarTitle, true)
    }

    override fun showTrainingDate(date: Calendar) {
        trainingDate = date
        dateText?.text = AppUtils.formatDateTimeWithWeekDay(date)
    }
    override fun showExercises(list: List<ExerciseInTraining>?) = exercisesAdapter?.setList(list, listView)
    override fun showChampions(list: List<String>?, selectedIndex: Int) = AppUtils.initSpinner(champion, list, selectedIndex, this)
    override fun showHeroes(list: List<String>?, selectedIndex: Int) = AppUtils.initSpinner(hero, list, selectedIndex, this)

    override fun showDateDialog(date: Calendar, minDate: Calendar, maxDate: Calendar) {
        trainingDate = date
        val dialog = DatePickerDialog(this,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth -> onDateSet(date, year, month, dayOfMonth) },
            date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH))
        dialog.datePicker.minDate = minDate.timeInMillis
        dialog.datePicker.maxDate = maxDate.timeInMillis
        dialog.show()
    }

    override fun showDeleteTrainingBtn(show: Boolean) {
        deleteTrainingBtnVisibility = show
        deleteTrainingBtn?.isVisible = deleteTrainingBtnVisibility
    }

    override fun closeScreen() = finish()
    //</editor-fold>


    //<editor-fold desc="Private methods">
    private fun initUi() {
        dateInput?.setOnClickListener { presenter.dateDialogRequested(trainingDate) }
        addExercise?.setOnClickListener {
            ExerciseDialog.showDialog(supportFragmentManager, presenter.provideTrainingId(), null, this)
        }
    }

    private fun updateTrainingData() {
        val championIndex = champion?.selectedItemPosition
        val heroIndex = hero?.selectedItemPosition
        if (championIndex == null || heroIndex == null || trainingDate == null) return
        presenter.updateTrainingData(championIndex, heroIndex, trainingDate)
    }

    private fun initList() {
        exercisesAdapter = ExercisesInTrainingAdapter(presenter.provideExercises(), Consumer {
            ExerciseDialog.showDialog(supportFragmentManager, presenter.provideTrainingId(), it.id, this)
        })
        listView?.layoutManager = CustomLinearLayoutManager(this)
        listView?.adapter = exercisesAdapter
    }

    private fun onDateSet(date: Calendar, year: Int, month: Int, dayOfMonth: Int) {
        val dialog = TimePickerDialog(this,
            TimePickerDialog.OnTimeSetListener { _, hour, minute -> onTimeSet(year, month, dayOfMonth, hour, minute) },
            date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE), true)
        dialog.show()
    }

    private fun onTimeSet(year: Int, month: Int, dayOfMonth: Int, hour: Int, minute: Int) {
        Logger.d(LOG_TAG, "onTimeSet. year=$year, month=$month, dayOfMonth=$dayOfMonth, hour=$hour, minute=$minute")
        val hourMinute = presenter.roundMinutesInHalfHourIntervals(hour, minute)
        val date = Calendar.getInstance()
        date.set(Calendar.YEAR, year)
        date.set(Calendar.MONTH, month)
        date.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        date.set(Calendar.HOUR_OF_DAY, hourMinute.hour)
        date.set(Calendar.MINUTE, hourMinute.minute)
        date.set(Calendar.SECOND, 0)
        showTrainingDate(date)
        updateTrainingData()
    }
    //</editor-fold>


    companion object {
        private const val LOG_TAG = "TrainingDetailsActivity"

        fun newInstance(context: Context, trainingId: Long?) {
            val intent = Intent(context, TrainingDetailsActivity::class.java)
            if (trainingId != null) intent.putExtra(TRAINING_ID, trainingId)
            context.startActivity(intent)
        }
    }
}