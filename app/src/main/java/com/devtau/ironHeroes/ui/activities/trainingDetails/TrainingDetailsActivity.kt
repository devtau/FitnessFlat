package com.devtau.ironHeroes.ui.activities.trainingDetails

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.data.model.Hero
import com.devtau.ironHeroes.data.model.Training
import com.devtau.ironHeroes.ui.DependencyRegistry
import com.devtau.ironHeroes.ui.activities.ViewSubscriberActivity
import com.devtau.ironHeroes.util.AppUtils
import com.devtau.ironHeroes.util.Constants
import com.devtau.ironHeroes.util.Constants.TRAINING_ID
import com.devtau.ironHeroes.util.Logger
import io.reactivex.functions.Action
import kotlinx.android.synthetic.main.activity_training_details.*
import java.util.*

class TrainingDetailsActivity: ViewSubscriberActivity(),
    TrainingDetailsView {

    var presenter: TrainingDetailsPresenter? = null
    private var champions: List<Hero>? = null
    private var heroes: List<Hero>? = null


    //<editor-fold desc="Framework overrides">
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_training_details)
        DependencyRegistry().inject(this)
        initUi()
    }

    override fun onStart() {
        super.onStart()
        presenter?.restartLoaders()
    }

    override fun onStop() {
        super.onStop()
        presenter?.onStop()
    }

    override fun onBackPressed() {
        presenter?.onBackPressed(Action { super.onBackPressed() })
    }
    //</editor-fold>


    //<editor-fold desc="View overrides">
    override fun showMsg(msgId: Int, confirmedListener: Action?) = showMsg(getString(msgId), confirmedListener)
    override fun showMsg(msg: String, confirmedListener: Action?) = AppUtils.alertD(LOG_TAG, msg, this, confirmedListener)

    override fun showScreenTitle(newTraining: Boolean) {
        val toolbarTitle = if (newTraining) R.string.training_add else R.string.training_edit
        AppUtils.initToolbar(this, toolbarTitle, true)
    }

    override fun showBirthdayNA() {
        dateText?.text = AppUtils.formatDate(null)
    }

    override fun showTrainingDetails(training: Training?) {
        fun updateInputField(input: TextView?, value: String?) {
            if (input != null && input.text?.toString() != value) {
                input.setText(value)
                if (input is EditText) input.setSelection(value?.length ?: 0)
            }
        }

        Logger.d(LOG_TAG, "showTrainingDetails. training=$training")
        updateInputField(dateText, AppUtils.formatDate(training?.date))
    }

    override fun showChampions(list: List<Hero>?, selectedChampionId: Long) {
        champions = list
        initSpinner(champion, list, selectedChampionId)
    }

    override fun showHeroes(list: List<Hero>?, selectedHeroId: Long) {
        heroes = list
        initSpinner(hero, list, selectedHeroId)
    }

    override fun onDateSet(date: Calendar) {
        dateText?.text = AppUtils.formatDate(date)
        updateTrainingData("dateText", dateText?.text?.toString())
    }

    override fun showDeleteTrainingBtn(show: Boolean) {
        fab?.postDelayed({ if (show) fab.show() else fab.hide() }, Constants.STANDARD_DELAY_MS)
        fab.setOnClickListener { presenter?.deleteTraining() }
    }

    override fun closeScreen() = finish()
    //</editor-fold>


    //<editor-fold desc="Private methods">
    private fun initUi() {
        dateInput?.setOnClickListener {
            presenter?.showDateDialog(this, AppUtils.parseDate(dateText?.text?.toString()).timeInMillis)
        }
    }

    private fun updateTrainingData(field: String, value: String?) {
        Logger.d(LOG_TAG, "updateTrainingData. new data in $field detected. value=$value")
        presenter?.updateTrainingData(
            champions?.get(champion.selectedItemPosition)?.id,
            heroes?.get(hero.selectedItemPosition)?.id,
            AppUtils.parseDate(dateText?.text?.toString()).timeInMillis)
    }

    private fun initSpinner(spinner: Spinner?, list: List<Hero>?, selectedHeroId: Long) {
        if (spinner == null || list == null) return

        val spinnerStrings = ArrayList<String>()
        var selectedItemIndex = 0
        for (i in list.indices) {
            val next = list[i]
            spinnerStrings.add(next.getName())
            if (next.id == selectedHeroId) selectedItemIndex = i
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerStrings)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.setSelection(selectedItemIndex)
        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) =
                updateTrainingData("spinner", spinner.selectedItem.toString())
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
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