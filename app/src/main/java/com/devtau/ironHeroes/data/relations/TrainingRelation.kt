package com.devtau.ironHeroes.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.devtau.ironHeroes.data.model.Hero
import com.devtau.ironHeroes.data.model.Champion
import com.devtau.ironHeroes.data.model.Training

class TrainingRelation {

    @Embedded
    lateinit var training: Training

    @Relation(parentColumn = "championId", entityColumn = "id")
    lateinit var champion: Champion

    @Relation(parentColumn = "heroId", entityColumn = "id")
    lateinit var hero: Hero


    companion object {
        fun convertToTrainings(list: List<TrainingRelation>): List<Training> {
            val trainings = ArrayList<Training>()
            for (next in list) {
                next.training.champion = next.champion
                next.training.hero = next.hero
                trainings.add(next.training)
            }
            return trainings
        }
    }
}