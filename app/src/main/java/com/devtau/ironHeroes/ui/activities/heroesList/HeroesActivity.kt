package com.devtau.ironHeroes.ui.activities.heroesList

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.adapters.CustomLinearLayoutManager
import com.devtau.ironHeroes.adapters.HeroesAdapter
import com.devtau.ironHeroes.data.model.Hero
import com.devtau.ironHeroes.enums.HumanType
import com.devtau.ironHeroes.ui.DependencyRegistry
import com.devtau.ironHeroes.ui.activities.ViewSubscriberActivity
import com.devtau.ironHeroes.ui.activities.heroDetails.HeroDetailsActivity
import com.devtau.ironHeroes.util.AppUtils
import com.devtau.ironHeroes.util.Constants
import com.devtau.ironHeroes.util.Constants.HUMAN_TYPE
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.activity_heroes.*

class HeroesActivity: ViewSubscriberActivity(), HeroesView {

    lateinit var presenter: HeroesPresenter
    private var adapter: HeroesAdapter? = null


    //<editor-fold desc="Framework overrides">
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_heroes)
        DependencyRegistry().inject(this)
        val toolbarTitle = when (presenter.provideHumanType()) {
            HumanType.HERO -> R.string.heroes
            HumanType.CHAMPION -> R.string.champions
        }
        AppUtils.initToolbar(this, toolbarTitle, true)
        initUi()
        initList()
    }

    override fun onStart() {
        super.onStart()
        presenter.restartLoaders()
    }

    override fun onStop() {
        super.onStop()
        presenter.onStop()
    }
    //</editor-fold>


    //<editor-fold desc="View overrides">
    override fun getLogTag() = LOG_TAG
    override fun updateHeroes(list: List<Hero>?) = adapter?.setList(list)
    //</editor-fold>


    //<editor-fold desc="Private methods">
    private fun initUi() {
        listView?.postDelayed({ fab.show() }, Constants.STANDARD_DELAY_MS)
        fab.setOnClickListener { HeroDetailsActivity.newInstance(this, null, presenter.provideHumanType()) }
    }

    private fun initList() {
        adapter = HeroesAdapter(null, Consumer {
            HeroDetailsActivity.newInstance(this, it.id, presenter.provideHumanType())
        })
        listView?.layoutManager = CustomLinearLayoutManager(this)
        listView?.adapter = adapter
    }
    //</editor-fold>


    companion object {
        private const val LOG_TAG = "HeroesActivity"

        fun newInstance(context: Context, humanType: HumanType) {
            val intent = Intent(context, HeroesActivity::class.java)
            intent.putExtra(HUMAN_TYPE, humanType)
            context.startActivity(intent)
        }
    }
}