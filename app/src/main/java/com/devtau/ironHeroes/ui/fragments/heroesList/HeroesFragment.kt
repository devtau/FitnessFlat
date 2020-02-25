package com.devtau.ironHeroes.ui.fragments.heroesList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.adapters.HeroesAdapter
import com.devtau.ironHeroes.data.model.Hero
import com.devtau.ironHeroes.enums.HumanType
import com.devtau.ironHeroes.ui.Coordinator
import com.devtau.ironHeroes.ui.DependencyRegistry
import com.devtau.ironHeroes.ui.fragments.BaseFragment
import com.devtau.ironHeroes.ui.fragments.initActionBar
import com.devtau.ironHeroes.util.Constants
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.fragment_heroes.*

class HeroesFragment: BaseFragment(), HeroesContract.View {

    private lateinit var presenter: HeroesContract.Presenter
    private lateinit var coordinator: Coordinator
    private var adapter: HeroesAdapter? = null


    //<editor-fold desc="Framework overrides">
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DependencyRegistry.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_heroes, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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


    //<editor-fold desc="Interface overrides">
    override fun getLogTag() = LOG_TAG

    override fun initActionbar() = activity?.initActionBar(when (presenter.provideHumanType()) {
        HumanType.HERO -> R.string.trainees
        HumanType.CHAMPION -> R.string.trainers
    })

    override fun updateHeroes(list: List<Hero>?) = adapter?.setList(list)
    //</editor-fold>


    fun configureWith(presenter: HeroesContract.Presenter, coordinator: Coordinator) {
        this.presenter = presenter
        this.coordinator = coordinator
    }


    //<editor-fold desc="Private methods">
    private fun initUi() {
        listView?.postDelayed({ fab.show() }, Constants.STANDARD_DELAY_MS)
        fab.setOnClickListener { coordinator.launchHeroDetails(it, null, presenter.provideHumanType()) }
    }

    private fun initList() {
        adapter = HeroesAdapter(null, Consumer {
            coordinator.launchHeroDetails(listView, it.id, presenter.provideHumanType())
        })
        listView?.adapter = adapter
    }
    //</editor-fold>


    companion object {
        private const val LOG_TAG = "HeroesActivity"
    }
}