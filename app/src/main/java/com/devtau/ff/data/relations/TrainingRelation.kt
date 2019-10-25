package com.devtau.ff.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.devtau.ff.data.model.Client
import com.devtau.ff.data.model.Trainer
import com.devtau.ff.data.model.Training

class TrainingRelation {

    @Embedded
    lateinit var training: Training

    @Relation(parentColumn = "trainerId", entityColumn = "id")
    lateinit var trainer: Trainer

    @Relation(parentColumn = "clientId", entityColumn = "id")
    lateinit var client: Client


    companion object {
        fun convertToTrainings(list: List<TrainingRelation>): List<Training> {
            val trainings = ArrayList<Training>()
            for (next in list) {
                next.training.trainer = next.trainer
                next.training.client = next.client
                trainings.add(next.training)
            }
            return trainings
        }
    }
}