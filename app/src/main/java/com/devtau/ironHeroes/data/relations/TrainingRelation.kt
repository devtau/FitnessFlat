package com.devtau.ironHeroes.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.devtau.ironHeroes.data.model.Hero
import com.devtau.ironHeroes.data.model.Training

class TrainingRelation {

    @Embedded
    lateinit var training: Training

    @Relation(parentColumn = "championId", entityColumn = "heroId")
    lateinit var champion: Hero

    @Relation(parentColumn = "heroId", entityColumn = "heroId")
    lateinit var hero: Hero

    fun convert(): Training {
        training.champion = champion
        training.hero = hero
        return training
    }
}