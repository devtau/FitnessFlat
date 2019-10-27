package com.devtau.ironHeroes.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.adapters.viewHolders.TrainingsViewHolder
import com.devtau.ironHeroes.data.model.Training
import com.devtau.ironHeroes.util.AppUtils
import com.devtau.ironHeroes.util.Logger
import io.reactivex.functions.Consumer

class TrainingsAdapter(
    private var trainings: List<Training>?,
    private val listener: Consumer<Training>
): RecyclerView.Adapter<TrainingsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainingsViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.list_item_training, parent, false)
        return TrainingsViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrainingsViewHolder, position: Int) {
        val training = trainings?.get(position) ?: return
        Logger.d(LOG_TAG, "onBindViewHolder. training=$training")
        holder.date.text = AppUtils.formatDateWithWeekDay(training.date)
        holder.root.setOnClickListener { listener.accept(training) }
    }

    override fun getItemCount(): Int = trainings?.size ?: 0


    fun setList(list: List<Training>?) {
        this.trainings = list
        notifyDataSetChanged()
    }


    companion object {
        private const val LOG_TAG = "TrainingsAdapter"
    }
}