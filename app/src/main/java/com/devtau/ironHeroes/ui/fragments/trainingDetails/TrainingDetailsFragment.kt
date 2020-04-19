package com.devtau.ironHeroes.ui.fragments.trainingDetails

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.adapters.ExercisesInTrainingAdapter
import com.devtau.ironHeroes.data.model.wrappers.DatePickerDialogDataWrapper
import com.devtau.ironHeroes.databinding.FragmentTrainingDetailsBinding
import com.devtau.ironHeroes.ui.fragments.BaseFragment
import com.devtau.ironHeroes.ui.fragments.getViewModelFactory
import com.devtau.ironHeroes.ui.fragments.initActionBar
import com.devtau.ironHeroes.util.EventObserver
import com.devtau.ironHeroes.util.Logger
import com.devtau.ironHeroes.util.showDialog
import io.reactivex.functions.Action
import java.util.*

class TrainingDetailsFragment: BaseFragment() {

    private val _viewModel by viewModels<TrainingDetailsViewModel> { getViewModelFactory() }


    //<editor-fold desc="Framework overrides">
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentTrainingDetailsBinding.inflate(inflater, container, false).apply {
            viewModel = _viewModel
            lifecycleOwner = viewLifecycleOwner
            _viewModel.subscribeToVM()

            initUi()
            listView.adapter = ExercisesInTrainingAdapter(_viewModel)
        }
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_training_details, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.delete -> {
            view?.showDialog(LOG_TAG, R.string.confirm_delete, Action {
                _viewModel.deleteTraining()
            })
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
    //</editor-fold>


    //<editor-fold desc="Private methods">
    private fun FragmentTrainingDetailsBinding.initUi() {
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
                _viewModel.onExerciseMoved(fromPosition, toPosition)
                recyclerView.adapter?.notifyItemMoved(fromPosition, toPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {/*NOP*/}
        })
        itemTouchHelper.attachToRecyclerView(listView)
    }

    private fun TrainingDetailsViewModel.subscribeToVM() {
        toolbarTitle.observe(viewLifecycleOwner, EventObserver {
            activity?.initActionBar(it)
        })

        showDateDialog.observe(viewLifecycleOwner, EventObserver {
            showDateDialog(it)
        })

        openExerciseEvent.observe(viewLifecycleOwner, EventObserver {
            coordinator.showExerciseFromTraining(view, it)
        })

        closeScreenEvent.observe(viewLifecycleOwner, EventObserver {
            activity?.onBackPressed()
        })
    }

    private fun showDateDialog(wrapper: DatePickerDialogDataWrapper)  {
        val context = context ?: return
        val dialog = DatePickerDialog(context,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth -> onDateSet(wrapper.selectedDate, year, month, dayOfMonth) },
            wrapper.selectedDate.get(Calendar.YEAR), wrapper.selectedDate.get(Calendar.MONTH), wrapper.selectedDate.get(Calendar.DAY_OF_MONTH))
        dialog.datePicker.minDate = wrapper.minDate.timeInMillis
        dialog.datePicker.maxDate = wrapper.maxDate.timeInMillis
        dialog.show()
    }

    private fun onDateSet(date: Calendar, year: Int, month: Int, dayOfMonth: Int) {
        val dialog = TimePickerDialog(context,
            TimePickerDialog.OnTimeSetListener { _, hour, minute -> onTimeSet(year, month, dayOfMonth, hour, minute) },
            date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE), true)
        dialog.show()
    }

    private fun onTimeSet(year: Int, month: Int, dayOfMonth: Int, hour: Int, minute: Int) {
        Logger.d(LOG_TAG, "onTimeSet. year=$year, month=$month, dayOfMonth=$dayOfMonth, hour=$hour, minute=$minute")
        _viewModel.updateTrainingDate(year, month, dayOfMonth, hour, minute)
    }
    //</editor-fold>


    companion object {
        private const val LOG_TAG = "TrainingDetailsFragment"
    }
}