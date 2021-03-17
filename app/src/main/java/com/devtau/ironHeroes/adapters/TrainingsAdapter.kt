package com.devtau.ironHeroes.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.devtau.ironHeroes.data.model.Training
import com.devtau.ironHeroes.databinding.ListItemTrainingBinding
import com.devtau.ironHeroes.ui.fragments.trainingsList.TrainingsViewModel

class TrainingsAdapter(
    private val viewModel: TrainingsViewModel
): ListAdapter<Training, TrainingsAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder.from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(viewModel, item)
    }


    class ViewHolder private constructor(
        private val binding: ListItemTrainingBinding
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(viewModel: TrainingsViewModel, item: Training) {
            binding.viewModel = viewModel
            binding.training = item
            binding.executePendingBindings()
        }


        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemTrainingBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }


    private class DiffCallback: DiffUtil.ItemCallback<Training>() {
        override fun areItemsTheSame(oldItem: Training, newItem: Training) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Training, newItem: Training) = oldItem == newItem
    }
}