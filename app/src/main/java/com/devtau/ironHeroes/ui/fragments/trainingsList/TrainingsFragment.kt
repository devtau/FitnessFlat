package com.devtau.ironHeroes.ui.fragments.trainingsList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import androidx.recyclerview.widget.RecyclerView
import com.devtau.ironHeroes.Coordinator
import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.adapters.CustomLinearLayoutManager
import com.devtau.ironHeroes.adapters.TrainingsAdapter
import com.devtau.ironHeroes.data.model.Training
import com.devtau.ironHeroes.ui.DependencyRegistry
import com.devtau.ironHeroes.ui.fragments.ViewSubscriberFragment
import com.devtau.ironHeroes.util.AppUtils
import com.devtau.ironHeroes.util.Constants
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.reactivex.functions.Consumer

class TrainingsFragment: ViewSubscriberFragment(), TrainingsView {

    private lateinit var presenter: TrainingsPresenter
    private lateinit var coordinator: Coordinator
    private var adapter: TrainingsAdapter? = null
    private var championContainer: View? = null
    private var heroContainer: View? = null
    private var champion: Spinner? = null
    private var hero: Spinner? = null
    private var listView: RecyclerView? = null
    private var fab: FloatingActionButton? = null


    //<editor-fold desc="Framework overrides">
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DependencyRegistry.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_trainings, container, false)
        initUi(root)
        initList()
        return root
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


    //<editor-fold desc="View overrides">
    override fun getLogTag() = LOG_TAG
    override fun updateTrainings(list: List<Training>?) {
        adapter?.setList(list, listView)
    }

    override fun showChampions(list: List<String>?, selectedIndex: Int) =
        AppUtils.initSpinner(champion, list, selectedIndex, context)

    override fun showHeroes(list: List<String>?, selectedIndex: Int) =
        AppUtils.initSpinner(hero, list, selectedIndex, context)
    //</editor-fold>


    fun configureWith(presenter: TrainingsPresenter, coordinator: Coordinator) {
        this.presenter = presenter
        this.coordinator = coordinator
    }

    fun updateSpinnersVisibility() {
        championContainer?.visibility = if (presenter.isChampionFilterNeeded()) View.VISIBLE else View.GONE
        heroContainer?.visibility = if (presenter.isHeroFilterNeeded()) View.VISIBLE else View.GONE
    }


    //<editor-fold desc="Private methods">
    private fun initUi(root: View) {
        championContainer = root.findViewById(R.id.championContainer)
        heroContainer = root.findViewById(R.id.heroContainer)
        champion = root.findViewById(R.id.champion)
        hero = root.findViewById(R.id.hero)
        listView = root.findViewById(R.id.listView)
        fab = root.findViewById(R.id.fab)

        listView?.postDelayed({ fab?.show() }, Constants.STANDARD_DELAY_MS)
        fab?.setOnClickListener { coordinator.launchTrainingDetailsActivity(context, null) }
        updateSpinnersVisibility()
    }

    private fun initList() {
        val context = context ?: return
        adapter = TrainingsAdapter(presenter.provideTrainings(), Consumer {
            coordinator.launchTrainingDetailsActivity(context, it.id)
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