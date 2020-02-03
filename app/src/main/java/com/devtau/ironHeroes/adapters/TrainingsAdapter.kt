package com.devtau.ironHeroes.adapters

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.data.model.Training
import com.devtau.ironHeroes.util.Animator
import com.devtau.ironHeroes.util.AppUtils
import com.devtau.ironHeroes.util.Logger
import com.devtau.ironHeroes.util.Threading
import io.reactivex.functions.Consumer
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.list_item_training.*

class TrainingsAdapter(
    private var trainings: List<Training>?,
    private val listener: Consumer<Training>
): RecyclerView.Adapter<TrainingsAdapter.TrainingsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainingsViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.list_item_training, parent, false)
        return TrainingsViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrainingsViewHolder, position: Int) {
        val training = trainings?.get(position) ?: return
        Logger.v(LOG_TAG, "onBindViewHolder. training=$training")
        Glide.with(holder.championImage).load(
            if (!TextUtils.isEmpty(training.champion?.avatarUrl)) training.champion?.avatarUrl
            else if (training.champion?.avatarId != null) training.champion?.avatarId
            else null)
            .transform(CircleCrop())
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(holder.championImage)
        holder.date.text = AppUtils.formatDateTimeWithWeekDay(training.date)
        Glide.with(holder.championImage).load(
            if (!TextUtils.isEmpty(training.hero?.avatarUrl)) training.hero?.avatarUrl
            else if (training.hero?.avatarId != null) training.hero?.avatarId
            else null)
            .transform(CircleCrop())
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(holder.heroImage)
        holder.containerView.setOnClickListener { listener.accept(training) }
    }

    override fun getItemCount(): Int = trainings?.size ?: 0


    fun setList(list: List<Training>?, listView: RecyclerView?) {
        if (itemCount == 0) {
            Threading.dispatchMainDelayed(Consumer {
                listView?.visibility = View.INVISIBLE
                this.trainings = list
                notifyDataSetChanged()
                val unbounded = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                listView?.measure(unbounded, unbounded)
                Animator.animateDropdownSlide(listView, listView?.measuredHeight, Animator.ANIMATION_LENGTH_MED, true)
            }, Animator.ANIMATION_LENGTH_MED)
        } else {
            this.trainings = list
            notifyDataSetChanged()
        }
    }


    class TrainingsViewHolder(override val containerView: View):
        RecyclerView.ViewHolder(containerView), LayoutContainer


    companion object {
        private const val LOG_TAG = "TrainingsAdapter"
    }
}