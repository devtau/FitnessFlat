package com.devtau.ironHeroes.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.devtau.ironHeroes.data.model.ExerciseInTraining
import com.devtau.ironHeroes.databinding.ListItemExerciseInTrainingBinding
import com.devtau.ironHeroes.ui.fragments.trainingDetails.TrainingDetailsViewModel

class ExercisesInTrainingAdapter(
    private val viewModel: TrainingDetailsViewModel
): ListAdapter<ExerciseInTraining, ExercisesInTrainingAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder.from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(viewModel, item)
    }


    class ViewHolder private constructor(
        private val binding: ListItemExerciseInTrainingBinding
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(viewModel: TrainingDetailsViewModel, item: ExerciseInTraining) {
            binding.viewModel = viewModel
            binding.exerciseInTraining = item
            binding.executePendingBindings()
        }


        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemExerciseInTrainingBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }


    private class DiffCallback: DiffUtil.ItemCallback<ExerciseInTraining>() {
        override fun areItemsTheSame(oldItem: ExerciseInTraining, newItem: ExerciseInTraining) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: ExerciseInTraining, newItem: ExerciseInTraining) = oldItem == newItem
    }
}