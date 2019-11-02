package com.devtau.ironHeroes.adapters

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.adapters.viewHolders.HeroesViewHolder
import com.devtau.ironHeroes.data.model.Hero
import com.devtau.ironHeroes.util.Logger
import io.reactivex.functions.Consumer

class HeroesAdapter(
    private var heroes: List<Hero>?,
    private val listener: Consumer<Hero>
): RecyclerView.Adapter<HeroesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeroesViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.list_item_hero, parent, false)
        return HeroesViewHolder(view)
    }

    override fun onBindViewHolder(holder: HeroesViewHolder, position: Int) {
        val hero = heroes?.get(position) ?: return
        Logger.v(LOG_TAG, "onBindViewHolder. hero=$hero")
        Glide.with(holder.context).load(
            if (!TextUtils.isEmpty(hero.avatarUrl)) hero.avatarUrl
            else if (hero.avatarId != null) hero.avatarId
            else null)
            .transition(DrawableTransitionOptions.withCrossFade()).into(holder.image)
        holder.name.text = hero.getName()
        holder.root.setOnClickListener { listener.accept(hero) }
    }

    override fun getItemCount(): Int = heroes?.size ?: 0


    fun setList(list: List<Hero>?) {
        heroes = list
        notifyDataSetChanged()
    }


    companion object {
        private const val LOG_TAG = "HeroesAdapter"
    }
}