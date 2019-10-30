package com.devtau.ironHeroes.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.devtau.ironHeroes.data.model.Hero
import com.devtau.ironHeroes.data.model.Training

class TrainingRelation {

    @Embedded
    lateinit var training: Training

    @Relation(parentColumn = "championId", entityColumn = "id")
    lateinit var champion: Hero

    @Relation(parentColumn = "heroId", entityColumn = "id")
    lateinit var hero: Hero

    fun convert(): Training {
        training.champion = champion
        training.hero = hero
        return training
    }


    companion object {
        fun convertList(list: List<TrainingRelation>): List<Training> {
            val trainings = ArrayList<Training>()
            for (next in list) trainings.add(next.convert())
            return trainings
        }
    }
}