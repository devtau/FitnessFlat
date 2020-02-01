package com.devtau.ironHeroes.ui.activities.heroesList

import android.os.Bundle
import com.devtau.ironHeroes.Coordinator
import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.adapters.CustomLinearLayoutManager
import com.devtau.ironHeroes.adapters.HeroesAdapter
import com.devtau.ironHeroes.data.model.Hero
import com.devtau.ironHeroes.enums.HumanType
import com.devtau.ironHeroes.ui.DependencyRegistry
import com.devtau.ironHeroes.ui.activities.ViewSubscriberActivity
import com.devtau.ironHeroes.util.AppUtils
import com.devtau.ironHeroes.util.Constants
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.activity_heroes.*

class HeroesActivity: ViewSubscriberActivity(), HeroesContract.View {

    private lateinit var presenter: HeroesContract.Presenter
    private lateinit var coordinator: Coordinator
    private var adapter: HeroesAdapter? = null


    //<editor-fold desc="Framework overrides">
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_heroes)
        DependencyRegistry.inject(this)
        val toolbarTitle = when (presenter.provideHumanType()) {
            HumanType.HERO -> R.string.trainees
            HumanType.CHAMPION -> R.string.trainers
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


    fun configureWith(presenter: HeroesContract.Presenter, coordinator: Coordinator) {
        this.presenter = presenter
        this.coordinator = coordinator
    }


    //<editor-fold desc="Private methods">
    private fun initUi() {
        listView?.postDelayed({ fab.show() }, Constants.STANDARD_DELAY_MS)
        fab.setOnClickListener { coordinator.launchHeroDetailsActivity(this, null, presenter.provideHumanType()) }
    }

    private fun initList() {
        adapter = HeroesAdapter(null, Consumer {
            coordinator.launchHeroDetailsActivity(this, it.id, presenter.provideHumanType())
        })
        listView?.layoutManager = CustomLinearLayoutManager(this)
        listView?.adapter = adapter
    }
    //</editor-fold>


    companion object {
        private const val LOG_TAG = "HeroesActivity"
    }
}