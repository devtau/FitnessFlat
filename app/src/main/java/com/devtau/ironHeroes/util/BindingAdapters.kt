package com.devtau.ironHeroes.util

import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ImageView
import android.widget.Spinner
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.adapters.HeroesSpinnerAdapter
import com.devtau.ironHeroes.adapters.HeroesSpinnerAdapter.HeroSelectedListener
import com.devtau.ironHeroes.adapters.TrainingsAdapter
import com.devtau.ironHeroes.data.model.Hero
import com.devtau.ironHeroes.data.model.Training
import com.devtau.ironHeroes.enums.HumanType

@BindingAdapter("humanImage")
fun ImageView.loadImage(human: Hero?) {
    val resource: Any? = when {
        !TextUtils.isEmpty(human?.avatarUrl) -> human?.avatarUrl
        human?.avatarId != null -> human.avatarId
        else -> null
    }
    Glide.with(this).load(resource)
        .transform(CircleCrop())
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(this)
}

@BindingAdapter("listItems")
fun RecyclerView.setListItems(entries: List<Training>?) {
    entries ?: return
    (adapter as TrainingsAdapter).submitList(entries)
}

@BindingAdapter(value = ["spinnerItems", "selectedItem", "onItemSelectedListener"], requireAll = false)
fun Spinner.init(entries: List<Hero>?, selectedId: Long? = null, listener: HeroSelectedListener?) {
    entries ?: return
    this.adapter = HeroesSpinnerAdapter(this.context, entries)

    if (selectedId != null) {
        val position = (adapter as HeroesSpinnerAdapter).getItemPosition(selectedId)
        setSelection(position)
    }

    if (listener != null) {
        onItemSelectedListener = object: OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val humanType = if (getId() == R.id.champion) HumanType.CHAMPION else HumanType.HERO
                val selectedHero = (adapter as HeroesSpinnerAdapter).getItem(position)
                listener.onHeroSelected(selectedHero, humanType)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {/*NOP*/}
        }
    }
}