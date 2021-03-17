package com.devtau.ironHeroes.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.devtau.ironHeroes.data.model.Hero
import com.devtau.ironHeroes.databinding.ListItemHeroBinding
import com.devtau.ironHeroes.ui.fragments.heroesList.HeroesViewModel

class HeroesAdapter(
    private val viewModel: HeroesViewModel
): ListAdapter<Hero, HeroesAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder.from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(viewModel, item)
    }


    class ViewHolder private constructor(
        private val binding: ListItemHeroBinding
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(viewModel: HeroesViewModel, item: Hero) {
            binding.viewModel = viewModel
            binding.hero = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemHeroBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }


    private class DiffCallback: DiffUtil.ItemCallback<Hero>() {
        override fun areItemsTheSame(oldItem: Hero, newItem: Hero) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Hero, newItem: Hero) = oldItem == newItem
    }
}