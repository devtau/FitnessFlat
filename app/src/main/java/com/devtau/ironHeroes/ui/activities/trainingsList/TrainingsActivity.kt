package com.devtau.ironHeroes.ui.activities.trainingsList

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.devtau.ironHeroes.adapters.CustomLinearLayoutManager
import com.devtau.ironHeroes.adapters.TrainingsAdapter
import com.devtau.ironHeroes.data.model.Training
import com.devtau.ironHeroes.ui.DependencyRegistry
import com.devtau.ironHeroes.ui.activities.trainingDetails.TrainingDetailsActivity
import com.devtau.ironHeroes.util.AppUtils
import com.devtau.ironHeroes.util.Constants
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.activity_trainings.*
import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.ui.activities.ViewSubscriberActivity

class TrainingsActivity: ViewSubscriberActivity(), TrainingsView {

    lateinit var presenter: TrainingsPresenter
    private var adapter: TrainingsAdapter? = null


    //<editor-fold desc="Framework overrides">
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trainings)
        DependencyRegistry().inject(this)
        AppUtils.initToolbar(this, R.string.trainings, true)
        initUi()
        initList()
    }

    override fun onStart() {
        super.onStart()
        presenter.restartLoaders()
        subscribeField(champion, Consumer { applyFilter() })
        subscribeField(hero, Consumer { applyFilter() })
    }

    override fun onStop() {
        super.onStop()
        presenter.onStop()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_trainings, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.filter -> {
            filters?.visibility = if (filters?.visibility == View.VISIBLE) View.GONE else View.VISIBLE
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
    //</editor-fold>


    //<editor-fold desc="View overrides">
    override fun showMsg(msgId: Int, confirmedListener: Action?) = showMsg(getString(msgId, confirmedListener))
    override fun showMsg(msg: String, confirmedListener: Action?) = AppUtils.alertD(LOG_TAG, msg, this, confirmedListener)

    override fun updateTrainings(list: List<Training>?) {
        adapter?.setList(list, listView)
    }

    override fun showChampions(list: List<String>?, selectedIndex: Int) =
        AppUtils.initSpinner(champion, list, selectedIndex, this)

    override fun showHeroes(list: List<String>?, selectedIndex: Int) =
        AppUtils.initSpinner(hero, list, selectedIndex, this)
    //</editor-fold>


    //<editor-fold desc="Private methods">
    private fun initUi() {
        listView?.postDelayed({ fab.show() }, Constants.STANDARD_DELAY_MS)
        fab.setOnClickListener { TrainingDetailsActivity.newInstance(this, null) }
    }

    private fun initList() {
        adapter = TrainingsAdapter(presenter.provideTrainings(), Consumer {
            TrainingDetailsActivity.newInstance(this, it.id)
        })
        listView?.layoutManager = CustomLinearLayoutManager(this)
        listView?.adapter = adapter
    }

    private fun applyFilter() = presenter.filterAndUpdateList(champion?.selectedItemPosition ?: 0, hero?.selectedItemPosition ?: 0)
    //</editor-fold>


    companion object {
        private const val LOG_TAG = "TrainingsActivity"

        fun newInstance(context: Context) {
            val intent = Intent(context, TrainingsActivity::class.java)
            context.startActivity(intent)
        }
    }
}