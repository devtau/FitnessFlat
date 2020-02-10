package com.devtau.ironHeroes.ui.fragments.trainingsList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.adapters.CustomLinearLayoutManager
import com.devtau.ironHeroes.adapters.TrainingsAdapter
import com.devtau.ironHeroes.data.model.Training
import com.devtau.ironHeroes.ui.Coordinator
import com.devtau.ironHeroes.ui.DependencyRegistry
import com.devtau.ironHeroes.ui.fragments.ViewSubscriberFragment
import com.devtau.ironHeroes.util.Constants
import com.devtau.ironHeroes.util.SpinnerUtils
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.fragment_trainings.*

class TrainingsFragment: ViewSubscriberFragment(), TrainingsContract.View {

    private lateinit var presenter: TrainingsContract.Presenter
    private lateinit var coordinator: Coordinator
    private var adapter: TrainingsAdapter? = null


    //<editor-fold desc="Framework overrides">
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DependencyRegistry.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_trainings, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
    //</editor-fold>


    //<editor-fold desc="Interface overrides">
    override fun getLogTag() = LOG_TAG
    override fun initActionbar() = false

    override fun updateTrainings(list: List<Training>) {
        adapter?.setList(list, listView)
    }

    override fun showChampions(list: List<String>, selectedIndex: Int) =
        SpinnerUtils.initSpinner(champion, list, selectedIndex, context)

    override fun showHeroes(list: List<String>, selectedIndex: Int) =
        SpinnerUtils.initSpinner(hero, list, selectedIndex, context)
    //</editor-fold>


    fun configureWith(presenter: TrainingsContract.Presenter, coordinator: Coordinator) {
        this.presenter = presenter
        this.coordinator = coordinator
    }

    fun updateSpinnersVisibility() {
        championContainer?.visibility = if (presenter.isChampionFilterNeeded()) View.VISIBLE else View.GONE
        heroContainer?.visibility = if (presenter.isHeroFilterNeeded()) View.VISIBLE else View.GONE
    }


    //<editor-fold desc="Private methods">
    private fun initUi() {
        listView?.postDelayed({ fab?.show() }, Constants.STANDARD_DELAY_MS)
        fab?.setOnClickListener { coordinator.launchTrainingDetails(fab, null) }
        updateSpinnersVisibility()
    }

    private fun initList() {
        val context = context ?: return
        adapter = TrainingsAdapter(Consumer {
            coordinator.launchTrainingDetails(listView, it.id)
        })
        listView?.layoutManager = CustomLinearLayoutManager(context)
        listView?.adapter = adapter
    }

    private fun applyFilter() = presenter.filterAndUpdateList(champion?.selectedItemPosition ?: 0, hero?.selectedItemPosition ?: 0)
    //</editor-fold>


    companion object {
        private const val LOG_TAG = "TrainingsFragment"
    }
}