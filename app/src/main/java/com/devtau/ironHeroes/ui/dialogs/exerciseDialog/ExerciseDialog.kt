package com.devtau.ironHeroes.ui.dialogs.exerciseDialog

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.view.*
import com.devtau.ironHeroes.data.model.ExerciseInTraining
import com.devtau.ironHeroes.ui.DependencyRegistry
import com.devtau.ironHeroes.ui.dialogs.ViewSubscriberDialog
import com.devtau.ironHeroes.util.AppUtils
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.dialog_exercise.*
import android.app.Notification
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import com.devtau.ironHeroes.enums.ChannelStats
import com.devtau.ironHeroes.util.AlarmReceiver
import com.devtau.ironHeroes.util.Logger
import android.os.CountDownTimer
import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.util.Animator

class ExerciseDialog: ViewSubscriberDialog(),
    ExerciseContract.View {

    private lateinit var presenter: ExerciseContract.Presenter


    //<editor-fold desc="Framework overrides">
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DependencyRegistry.inject(this)
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
        subscribeField(exercise, Consumer {
            presenter.updatePreviousExerciseData(exercise?.selectedItemPosition ?: 0)
        })
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


    fun configureWith(presenter: ExerciseContract.Presenter) {
        this.presenter = presenter
    }


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

        one.setOnClickListener { startRecreationTimer(parseRecreationTime()) }
        two.setOnClickListener { startRecreationTimer(parseRecreationTime()) }
        three.setOnClickListener { startRecreationTimer(parseRecreationTime()) }
        four.setOnClickListener { startRecreationTimer(parseRecreationTime()) }
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

    private fun parseRecreationTime(): Int = recreationInput?.text?.toString()?.toIntOrNull() ?: 90

    private fun startRecreationTimer(restTimeSeconds: Int) {
        val futureMs = SystemClock.elapsedRealtime() + restTimeSeconds * 1000
        val formatter = getString(R.string.recreation_formatter)
        val seconds = resources.getQuantityString(R.plurals.seconds, restTimeSeconds, restTimeSeconds)
        val msg = String.format(formatter, seconds)
        val notification = getNotification(context, msg) ?: return
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra(AlarmReceiver.NOTIFICATION_ID, 1)
        intent.putExtra(AlarmReceiver.NOTIFICATION, notification)
        val pendingIntent = PendingIntent.getBroadcast(context, 100, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager?

        alarmManager?.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureMs, pendingIntent)


        var i = 0
        progressBar.progress = i
        val restTimeMs = restTimeSeconds * 1000L
        val countDownIntervalMs = 1000L
        val maxProgressValue = progressBar.max
        Animator.animateProgressBar(progressBar, 0f, maxProgressValue.toFloat(), restTimeMs)
        object: CountDownTimer(restTimeMs, countDownIntervalMs) {
            override fun onTick(msLeft: Long) {
                Logger.d(LOG_TAG, "CountDownTimer. Tick of Progress $i, $msLeft ms left")
                i++
            }

            override fun onFinish() {
                Logger.d(LOG_TAG, "CountDownTimer. onFinish")
//                progressBar.progress = maxProgressValue
            }
        }.start()
    }

    private fun getNotification(context: Context?, content: String): Notification? {
        context ?: return null
        return NotificationCompat.Builder(context, ChannelStats.DEFAULT_SOUND.id)
            .setContentTitle(getString(R.string.next_set))
            .setContentText(content)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher_round))
            .setSmallIcon(R.drawable.ic_workouts_white)
            .build()
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
        const val LOG_TAG = "ExerciseDialog"
    }
}