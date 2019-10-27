package com.devtau.ironHeroes.ui.activities.trainingDetails

import android.app.DatePickerDialog
import android.content.Context
import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.data.DataLayer
import com.devtau.ironHeroes.data.model.Hero
import com.devtau.ironHeroes.data.model.Training
import com.devtau.ironHeroes.rest.NetworkLayer
import com.devtau.ironHeroes.ui.DBSubscriber
import com.devtau.ironHeroes.util.AppUtils
import com.devtau.ironHeroes.util.Constants
import com.devtau.ironHeroes.util.Logger
import com.devtau.ironHeroes.util.PreferencesManager
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import java.util.*

class TrainingDetailsPresenterImpl(
    private val view: TrainingDetailsView,
    private val dataLayer: DataLayer,
    private val networkLayer: NetworkLayer,
    private val prefs: PreferencesManager?,
    private val trainingId: Long?
): DBSubscriber(), TrainingDetailsPresenter {

    private var training: Training? = null
    private var champions: List<Hero>? = null
    private var heroes: List<Hero>? = null


    //<editor-fold desc="Presenter overrides">
    override fun restartLoaders() {
        if (trainingId == null) {
            view.showScreenTitle(true)
            view.showBirthdayNA()
            view.showDeleteTrainingBtn(false)
        } else {
            dataLayer.getTrainingByIdAndClose(trainingId, Consumer {
                training = it
                view.showTrainingDetails(training)
                view.showScreenTitle(training == null)
                view.showDeleteTrainingBtn(training != null)
            })
        }
        disposeOnStop(dataLayer.getChampions(Consumer {
            champions = it
            view.showChampions(it, training?.championId ?: 0)
        }))
        disposeOnStop(dataLayer.getHeroes(Consumer {
            heroes = it
            view.showHeroes(it, training?.heroId ?: 0)
        }))
    }

    override fun updateTrainingData(championId: Long?, heroId: Long?, date: Long?) {
        val allPartsPresent = Training.allObligatoryPartsPresent(championId, heroId, date)
        val someFieldsChanged = training?.someFieldsChanged(championId, heroId, date) ?: true
        Logger.d(LOG_TAG, "updateTrainingData. allPartsPresent=$allPartsPresent, someFieldsChanged=$someFieldsChanged")
        if (allPartsPresent && someFieldsChanged) {
            training = Training(trainingId, championId!!, heroId!!, date!!)
            dataLayer.updateTrainings(listOf(training))
        }
    }

    override fun showDateDialog(context: Context, selectedDate: Long?) {
        val nowMinusCentury = Calendar.getInstance()
        nowMinusCentury.add(Calendar.YEAR, -100)
        val date = Calendar.getInstance()
        if (selectedDate != null) date.timeInMillis = training?.date ?: selectedDate

        val dialog = DatePickerDialog(context,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth -> onDateSet(year, month, dayOfMonth) },
            date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH))
        dialog.datePicker.minDate = nowMinusCentury.timeInMillis
        dialog.datePicker.maxDate = Calendar.getInstance().timeInMillis
        dialog.show()
    }

    override fun onBackPressed(action: Action) {
        if (training == null) {
            view.showMsg(R.string.training_not_saved, action)
        } else {
            action.run()
        }
    }

    override fun deleteTraining() {
        view.showMsg(R.string.confirm_delete, Action {
            dataLayer.deleteTrainings(listOf(training))
            view.closeScreen()
        })
    }
    //</editor-fold>


    private fun onDateSet(year: Int, month: Int, dayOfMonth: Int) {
        val date = Calendar.getInstance()
        date.set(Calendar.YEAR, year)
        date.set(Calendar.MONTH, month)
        date.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        date.set(Calendar.HOUR_OF_DAY, 0)
        date.set(Calendar.MINUTE, 0)
        date.set(Calendar.SECOND, 0)

        view.onDateSet(date)
    }


    companion object {
        private const val LOG_TAG = "TrainingDetailsPresenterImpl"
    }
}