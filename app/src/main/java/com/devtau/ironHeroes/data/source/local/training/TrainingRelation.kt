package com.devtau.ironHeroes.data.source.local.training

import androidx.room.Embedded
import androidx.room.Relation
import com.devtau.ironHeroes.data.model.Hero
import com.devtau.ironHeroes.data.model.Training
import timber.log.Timber

class TrainingRelation {

    @Embedded
    lateinit var training: Training

    @Relation(parentColumn = "championId", entityColumn = "heroId")
    var champion: Hero? = null

    @Relation(parentColumn = "heroId", entityColumn = "heroId")
    var hero: Hero? = null

    fun convert(): Training {
        val msg = when {
            champion == null -> "champion with id=${training.championId} not found in db"
            hero == null -> "hero with id=${training.heroId} not found in db"
            else -> null
        }

        if (msg != null) {
            Timber.e(msg)
            return training
        }
        training.champion = champion
        training.hero = hero
        return training
    }
}