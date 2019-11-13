package com.devtau.ironHeroes.ui.activities.launcher

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.data.model.Hero
import com.devtau.ironHeroes.enums.HumanType
import com.devtau.ironHeroes.ui.DependencyRegistry
import com.devtau.ironHeroes.ui.activities.DBViewerActivity
import com.devtau.ironHeroes.ui.activities.ViewSubscriberActivity
import com.devtau.ironHeroes.ui.activities.heroesList.HeroesActivity
import com.devtau.ironHeroes.ui.activities.statistics.StatisticsActivity
import com.devtau.ironHeroes.ui.activities.trainingsList.TrainingsActivity
import com.devtau.ironHeroes.util.AppUtils
import kotlinx.android.synthetic.main.activity_launcher.*

class LauncherActivity: ViewSubscriberActivity(), LauncherView {

    lateinit var presenter: LauncherPresenter


    //<editor-fold desc="Framework overrides">
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)
        DependencyRegistry().inject(this)
        AppUtils.initToolbar(this, R.string.choose_action, false)
        initUi()
    }

    override fun onStart() {
        super.onStart()
        presenter.restartLoaders()
    }

    override fun onStop() {
        super.onStop()
        presenter.onStop()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_launcher, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.openDB -> {
            DBViewerActivity.newInstance(this)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
    //</editor-fold>


    //<editor-fold desc="View overrides">
    override fun getLogTag() = LOG_TAG
    //</editor-fold>


    //<editor-fold desc="Private methods">
    private fun initUi() {
        heroes.setOnClickListener { HeroesActivity.newInstance(this, HumanType.HERO) }
        champions.setOnClickListener { HeroesActivity.newInstance(this, HumanType.CHAMPION) }
        trainings.setOnClickListener { TrainingsActivity.newInstance(this) }
        statistics.setOnClickListener { StatisticsActivity.newInstance(this, Hero.getMockHeroes()[0].id!!) }
    }
    //</editor-fold>


    companion object {
        private const val LOG_TAG = "LauncherActivity"
    }
}