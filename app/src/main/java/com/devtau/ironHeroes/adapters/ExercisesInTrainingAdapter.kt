package com.devtau.ironHeroes.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.data.model.ExerciseInTraining
import com.devtau.ironHeroes.util.Animator
import com.devtau.ironHeroes.util.Logger
import com.devtau.ironHeroes.util.Threading
import io.reactivex.functions.Consumer
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.list_item_exercise_in_training.*

class ExercisesInTrainingAdapter(
    private var exercises: List<ExerciseInTraining>?,
    private val listener: Consumer<ExerciseInTraining>
): RecyclerView.Adapter<ExercisesInTrainingAdapter.ExercisesInTrainingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExercisesInTrainingViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.list_item_exercise_in_training, parent, false)
        return ExercisesInTrainingViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExercisesInTrainingViewHolder, position: Int) {
        val exercise = exercises?.get(position) ?: return
        Logger.v(LOG_TAG, "onBindViewHolder. exercise=$exercise")
        holder.exercise.text = exercise.exercise?.name
        holder.weight.text = exercise.weight.toString()
        holder.repeats.text = exercise.repeats.toString()
        holder.count.text = exercise.count.toString()
        holder.containerView.setOnClickListener { listener.accept(exercise) }
    }

    override fun getItemCount(): Int = exercises?.size ?: 0


    fun setList(list: List<ExerciseInTraining>?, listView: RecyclerView?) {
        if (itemCount == 0) {
            Threading.dispatchMainDelayed(Consumer {
                listView?.visibility = View.INVISIBLE
                exercises = list
                notifyDataSetChanged()
                val unbounded = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                listView?.measure(unbounded, unbounded)
                Animator.animateDropdownSlide(listView, listView?.measuredHeight, Animator.ANIMATION_LENGTH_MED, true)
            }, Animator.ANIMATION_LENGTH_SHORT)
        } else {
            exercises = list
            notifyDataSetChanged()
            listView?.smoothScrollToPosition(itemCount)
        }
    }


    class ExercisesInTrainingViewHolder(override val containerView: View):
        RecyclerView.ViewHolder(containerView), LayoutContainer


    companion object {
        private const val LOG_TAG = "ExercisesInTrainingAdapter"
    }
}