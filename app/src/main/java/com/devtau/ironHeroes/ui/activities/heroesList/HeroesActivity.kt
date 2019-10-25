package com.devtau.ironHeroes.ui.activities.heroesList

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.adapters.CustomLinearLayoutManager
import com.devtau.ironHeroes.adapters.HeroesAdapter
import com.devtau.ironHeroes.data.model.Hero
import com.devtau.ironHeroes.ui.DependencyRegistry
import com.devtau.ironHeroes.ui.activities.DBViewerActivity
import com.devtau.ironHeroes.ui.activities.heroDetails.HeroDetailsActivity
import com.devtau.ironHeroes.util.AppUtils
import com.devtau.ironHeroes.util.Constants
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.activity_heroes.*

class HeroesActivity: AppCompatActivity(), HeroesView {

    var presenter: HeroesPresenterImpl? = null
    private var adapter: HeroesAdapter? = null


    //<editor-fold desc="Framework overrides">
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_heroes)
        DependencyRegistry().inject(this)
        AppUtils.initToolbar(this, R.string.heroes, false)
        initUi()
        initList()
    }

    override fun onStart() {
        super.onStart()
        presenter?.restartLoaders()
    }

    override fun onStop() {
        super.onStop()
        presenter?.onStop()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_heroes, menu)
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

    override fun updateHeroes(list: List<Hero>?) = adapter?.setList(list)
    //</editor-fold>


    //<editor-fold desc="Private methods">
    private fun initUi() {
        listView?.postDelayed({ fab.show() }, Constants.STANDARD_DELAY_MS)
        fab.setOnClickListener { HeroDetailsActivity.newInstance(this, null) }
    }

    private fun initList() {
        adapter = HeroesAdapter(presenter?.heroes, Consumer { HeroDetailsActivity.newInstance(this, it.id) })
        listView?.layoutManager = CustomLinearLayoutManager(this)
        listView?.adapter = adapter
    }
    //</editor-fold>


    companion object {
        private const val LOG_TAG = "HeroesActivity"
    }
}