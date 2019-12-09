package com.devtau.ironHeroes.ui.fragments.trainingsList

import android.os.Bundle
import android.view.*
import android.widget.Spinner
import androidx.recyclerview.widget.RecyclerView
import com.devtau.ironHeroes.adapters.CustomLinearLayoutManager
import com.devtau.ironHeroes.adapters.TrainingsAdapter
import com.devtau.ironHeroes.data.model.Training
import com.devtau.ironHeroes.ui.DependencyRegistry
import com.devtau.ironHeroes.ui.activities.trainingDetails.TrainingDetailsActivity
import com.devtau.ironHeroes.util.AppUtils
import com.devtau.ironHeroes.util.Constants
import io.reactivex.functions.Consumer
import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.ui.fragments.ViewSubscriberFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TrainingsFragment: ViewSubscriberFragment(), TrainingsView {

    lateinit var presenter: TrainingsPresenter
    private var adapter: TrainingsAdapter? = null
    private var champion: Spinner? = null
    private var hero: Spinner? = null
    private var listView: RecyclerView? = null
    private var fab: FloatingActionButton? = null


    //<editor-fold desc="Framework overrides">
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DependencyRegistry().inject(this)
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


    //<editor-fold desc="Private methods">
    private fun initUi(root: View) {
        val championContainer = root.findViewById<View>(R.id.championContainer)
        val heroContainer = root.findViewById<View>(R.id.heroContainer)
        champion = root.findViewById(R.id.champion)
        hero = root.findViewById(R.id.hero)
        listView = root.findViewById(R.id.listView)
        fab = root.findViewById(R.id.fab)

        listView?.postDelayed({ fab?.show() }, Constants.STANDARD_DELAY_MS)
        fab?.setOnClickListener { TrainingDetailsActivity.newInstance(context, null) }

        championContainer?.visibility = if (presenter.isChampionFilterNeeded()) View.VISIBLE else View.GONE
        heroContainer?.visibility = if (presenter.isHeroFilterNeeded()) View.VISIBLE else View.GONE
    }

    private fun initList() {
        val context = context ?: return
        adapter = TrainingsAdapter(presenter.provideTrainings(), Consumer {
            TrainingDetailsActivity.newInstance(context, it.id)
        })
        listView?.layoutManager = CustomLinearLayoutManager(context)
        listView?.adapter = adapter
    }

    private fun applyFilter() = presenter.filterAndUpdateList(champion?.selectedItemPosition ?: 0, hero?.selectedItemPosition ?: 0)
    //</editor-fold>


    companion object {
        const val FRAGMENT_TAG = "com.devtau.ironHeroes.ui.fragments.trainingsList.TrainingsFragment"
        private const val LOG_TAG = "TrainingsFragment"

        fun newInstance(): TrainingsFragment {
            val fragment = TrainingsFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}