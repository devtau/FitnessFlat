package com.devtau.ironHeroes.ui.activities.launcher

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.enums.HumanType
import com.devtau.ironHeroes.ui.DependencyRegistry
import com.devtau.ironHeroes.ui.activities.DBViewerActivity
import com.devtau.ironHeroes.ui.activities.heroesList.HeroesActivity
import com.devtau.ironHeroes.ui.activities.trainingsList.TrainingsActivity
import com.devtau.ironHeroes.util.AppUtils
import io.reactivex.functions.Action
import kotlinx.android.synthetic.main.activity_launcher.*

class LauncherActivity: AppCompatActivity(), LauncherView {

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
    override fun showMsg(msgId: Int, confirmedListener: Action?) = showMsg(getString(msgId, confirmedListener))
    override fun showMsg(msg: String, confirmedListener: Action?) = AppUtils.alertD(LOG_TAG, msg, this, confirmedListener)
    //</editor-fold>


    //<editor-fold desc="Private methods">
    private fun initUi() {
        heroes.setOnClickListener { HeroesActivity.newInstance(this, HumanType.HERO) }
        champions.setOnClickListener { HeroesActivity.newInstance(this, HumanType.CHAMPION) }
        trainings.setOnClickListener { TrainingsActivity.newInstance(this) }
    }
    //</editor-fold>


    companion object {
        private const val LOG_TAG = "LauncherActivity"
    }
}