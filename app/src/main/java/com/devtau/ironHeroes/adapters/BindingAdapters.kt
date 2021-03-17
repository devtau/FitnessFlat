package com.devtau.ironHeroes.adapters

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
import com.devtau.ironHeroes.adapters.IronSpinnerAdapter.ItemSelectedListener
import com.devtau.ironHeroes.data.model.ExerciseInTraining
import com.devtau.ironHeroes.data.model.Hero
import com.devtau.ironHeroes.data.model.SpinnerItem
import com.devtau.ironHeroes.data.model.Training
import com.devtau.ironHeroes.enums.HumanType
import timber.log.Timber

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
fun RecyclerView.setTrainingItems(entries: List<Training>?) {
    if (entries != null) (adapter as TrainingsAdapter).submitList(entries)
}

@BindingAdapter("listItems")
fun RecyclerView.setExerciseInTrainingItems(entries: List<ExerciseInTraining>?) {
    if (entries != null) (adapter as ExercisesInTrainingAdapter).submitList(entries)
}

@BindingAdapter("listItems")
fun RecyclerView.setHeroItems(entries: List<Hero>?) {
    if (entries != null) (adapter as HeroesAdapter).submitList(entries)
}

@BindingAdapter(value = ["spinnerItems", "selectedItem", "onItemSelectedListener", "withHeader"], requireAll = false)
fun Spinner.init(entries: List<SpinnerItem>?, selectedId: Long?, listener: ItemSelectedListener?, withHeader: Boolean?) {
    entries ?: return
    this.adapter = IronSpinnerAdapter(this.context, entries, withHeader ?: true)

    if (selectedId != null) {
        val position = (adapter as IronSpinnerAdapter).getItemPosition(selectedId)
        setSelection(position)
    }

    if (listener != null) {
        onItemSelectedListener = object: OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val humanType = when (getId()) {
                    R.id.champion -> HumanType.CHAMPION
                    R.id.hero -> HumanType.HERO
                    else -> null
                }
                val selected = (adapter as IronSpinnerAdapter).getItem(position)
                Timber.d("onItemSelected. id=${selected?.id}, humanType=$humanType")
                listener.onItemSelected(selected, humanType)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {/*NOP*/}
        }
    }
}