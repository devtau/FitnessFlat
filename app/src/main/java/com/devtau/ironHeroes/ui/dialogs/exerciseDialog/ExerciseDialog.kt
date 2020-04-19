package com.devtau.ironHeroes.ui.dialogs.exerciseDialog

import android.app.AlarmManager
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.view.*
import android.widget.ProgressBar
import androidx.core.app.NotificationCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.data.model.wrappers.ExerciseDataWrapper
import com.devtau.ironHeroes.databinding.DialogExerciseBinding
import com.devtau.ironHeroes.enums.ChannelStats
import com.devtau.ironHeroes.ui.fragments.getViewModelFactory
import com.devtau.ironHeroes.util.*

class ExerciseDialog: DialogFragment() {

    private val _viewModel by viewModels<ExerciseViewModel> { getViewModelFactory() }


    //<editor-fold desc="Framework overrides">
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding = DialogExerciseBinding.inflate(inflater, container, false).apply {
            viewModel = _viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        _viewModel.subscribeToVM(binding)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val window = dialog?.window ?: return
        val params = window.attributes
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        window.attributes = params as WindowManager.LayoutParams
    }
    //</editor-fold>


    //<editor-fold desc="Private methods">
    private fun ExerciseViewModel.subscribeToVM(binding: DialogExerciseBinding) {
        dismissDialog.observe(viewLifecycleOwner, EventObserver {
            dialog?.dismiss()
        })
        showPreviousExerciseData.observe(viewLifecycleOwner, EventObserver {
            binding.previousExerciseData.text = composePreviousExerciseDataString(it)
        })
        startRecreationTimer.observe(viewLifecycleOwner, EventObserver { number ->
            startRecreationTimer(parseRecreationTime(binding), binding.progressBar)
        })
    }

    private fun parseRecreationTime(binding: DialogExerciseBinding): Int =
        binding.recreationInput.text?.toString()?.toIntOrNull() ?: 90

    private fun startRecreationTimer(restTimeSeconds: Int, progressBar: ProgressBar) {
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

    private fun composePreviousExerciseDataString(data: ExerciseDataWrapper) =
        if (data.trainingDate == null || data.weight == null || data.repeats == null || data.count == null) {
            context?.getString(R.string.no_data)
        } else {
            val formatter = context?.getString(R.string.previous_training_data_formatter) ?: ""
            val dateFormatted = DateUtils.formatDateWithWeekDay(data.trainingDate)
            String.format(formatter, dateFormatted, data.weight.toString(), data.repeats.toString(), data.count.toString())
        }
    //</editor-fold>


    companion object {
        const val LOG_TAG = "ExerciseDialog"
    }
}