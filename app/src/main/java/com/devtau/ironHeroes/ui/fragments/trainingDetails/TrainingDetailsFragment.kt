package com.devtau.ironHeroes.ui.fragments.trainingDetails

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.adapters.ExercisesInTrainingAdapter
import com.devtau.ironHeroes.data.model.ExerciseInTraining
import com.devtau.ironHeroes.ui.Coordinator
import com.devtau.ironHeroes.ui.DependencyRegistry
import com.devtau.ironHeroes.ui.fragments.BaseFragment
import com.devtau.ironHeroes.ui.fragments.initActionBar
import com.devtau.ironHeroes.util.DateUtils
import com.devtau.ironHeroes.util.Logger
import com.devtau.ironHeroes.util.SpinnerUtils
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.fragment_training_details.*
import java.util.*

class TrainingDetailsFragment: BaseFragment(),
    TrainingDetailsContract.View {

    private lateinit var presenter: TrainingDetailsContract.Presenter
    private lateinit var coordinator: Coordinator
    private var exercisesAdapter: ExercisesInTrainingAdapter? = null
    private var trainingDate: Calendar? = null


    //<editor-fold desc="Framework overrides">
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DependencyRegistry.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_training_details, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        initList()
        setHasOptionsMenu(true)
    }

    override fun onStart() {
        super.onStart()
        presenter.restartLoaders()
        subscribeField(champion, Consumer { updateTrainingData() })
        subscribeField(hero, Consumer { updateTrainingData() })
    }

    override fun onStop() {
        super.onStop()
        presenter.onStop()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_training_details, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.delete -> {
            presenter.deleteTraining()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    //using navigation as menu action
//    override fun onOptionsItemSelected(item: MenuItem): Boolean
//            = NavigationUI.onNavDestinationSelected(item, view!!.findNavController())
//            || super.onOptionsItemSelected(item)
    //</editor-fold>


    //<editor-fold desc="Interface overrides">
    override fun getLogTag() = LOG_TAG
    override fun initActionbar() = false
    override fun showScreenTitle(newTraining: Boolean) {
        activity?.initActionBar(if (newTraining) R.string.training_add else R.string.training_edit)
    }

    override fun showTrainingDate(date: Calendar) {
        trainingDate = date
        dateText?.text = DateUtils.formatDateTimeWithWeekDay(date)
    }

    override fun showExercises(list: List<ExerciseInTraining>?) =
        exercisesAdapter?.setList(list, listView)

    override fun showChampions(list: List<String>?, selectedIndex: Int) =
        SpinnerUtils.initSpinner(champion, list, selectedIndex, context)

    override fun showHeroes(list: List<String>?, selectedIndex: Int) =
        SpinnerUtils.initSpinner(hero, list, selectedIndex, context)

    override fun showDateDialog(date: Calendar, minDate: Calendar, maxDate: Calendar) {
        val context = context ?: return
        trainingDate = date
        val dialog = DatePickerDialog(context,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth -> onDateSet(date, year, month, dayOfMonth) },
            date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH))
        dialog.datePicker.minDate = minDate.timeInMillis
        dialog.datePicker.maxDate = maxDate.timeInMillis
        dialog.show()
    }

    override fun closeScreen() = activity?.onBackPressed()

    override fun showNewExerciseDialog(position: Int) =
        coordinator.showExercise(view, presenter.provideTraining()?.heroId,
            presenter.provideTraining()?.id, null, position)
    //</editor-fold>


    fun configureWith(presenter: TrainingDetailsContract.Presenter, coordinator: Coordinator) {
        this.presenter = presenter
        this.coordinator = coordinator
    }


    //<editor-fold desc="Private methods">
    private fun initUi() {
        dateInput?.setOnClickListener { presenter.dateDialogRequested(trainingDate) }
        addExercise?.setOnClickListener { presenter.addExerciseClicked() }
    }

    private fun updateTrainingData() {
        val championIndex = champion?.selectedItemPosition
        val heroIndex = hero?.selectedItemPosition
        if (championIndex == null || heroIndex == null || trainingDate == null) return
        presenter.updateTrainingData(championIndex, heroIndex, trainingDate)
    }

    private fun initList() {
        exercisesAdapter = ExercisesInTrainingAdapter(presenter.provideExercises(), Consumer {
            coordinator.showExercise(view, presenter.provideTraining()?.heroId,
                presenter.provideTraining()?.id, it.id, it.position)
        })
        listView?.adapter = exercisesAdapter

        val itemTouchHelper = ItemTouchHelper(object: ItemTouchHelper.Callback() {
            override fun isLongPressDragEnabled() = true
            override fun isItemViewSwipeEnabled() = false

            override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                val swipeFlags = if (isItemViewSwipeEnabled) ItemTouchHelper.START or ItemTouchHelper.END else 0
                return makeMovementFlags(dragFlags, swipeFlags)
            }

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                if (viewHolder.itemViewType != target.itemViewType) return false
                val fromPosition = viewHolder.adapterPosition
                val toPosition = target.adapterPosition
                presenter.onExerciseMoved(fromPosition, toPosition)
                recyclerView.adapter?.notifyItemMoved(fromPosition, toPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//                val position = viewHolder.adapterPosition
//                items.remove(position)
//                listView.adapter?.notifyItemRemoved(position)
            }
        })
        itemTouchHelper.attachToRecyclerView(listView)
    }

    private fun onDateSet(date: Calendar, year: Int, month: Int, dayOfMonth: Int) {
        val dialog = TimePickerDialog(context,
            TimePickerDialog.OnTimeSetListener { _, hour, minute -> onTimeSet(year, month, dayOfMonth, hour, minute) },
            date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE), true)
        dialog.show()
    }

    private fun onTimeSet(year: Int, month: Int, dayOfMonth: Int, hour: Int, minute: Int) {
        Logger.d(LOG_TAG, "onTimeSet. year=$year, month=$month, dayOfMonth=$dayOfMonth, hour=$hour, minute=$minute")
        showTrainingDate(DateUtils.getRoundDate(year, month, dayOfMonth, hour, minute))
        updateTrainingData()
    }
    //</editor-fold>


    companion object {
        private const val LOG_TAG = "TrainingDetailsActivity"
    }
}