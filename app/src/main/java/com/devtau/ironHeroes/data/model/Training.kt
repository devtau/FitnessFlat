package com.devtau.ironHeroes.data.model

import androidx.room.Entity
import androidx.room.Index
import com.devtau.ironHeroes.util.AppUtils
import com.devtau.ironHeroes.util.Constants.EMPTY_OBJECT_ID

@Entity(
    tableName = "Trainings",
    indices = [Index("championId", "heroId")],
    ignoredColumns = ["champion", "hero"]
)
class Training(
    id: Long?,
    var championId: Long,
    var champion: Hero? = null,
    var heroId: Long,
    var hero: Hero? = null,
    var date: Long
): DataObject(id) {

    constructor(id: Long?, championId: Long, heroId: Long, date: Long): this(id, championId, null, heroId, null, date)


    fun someFieldsChanged(championId: Long?, heroId: Long?, date: Long?) =
        championId != this.championId
                || heroId != this.heroId
                || date != this.date

    override fun toString(): String =
        "championId=$championId, heroId=$heroId, date=${AppUtils.formatDate(date)}"

    companion object {
        fun allObligatoryPartsPresent(championId: Long?, heroId: Long?, date: Long?) =
            championId != null && heroId != null && date != null

        fun getMock() = listOf(
            Training(1, Hero.getMockChampions()[1].id ?: EMPTY_OBJECT_ID, Hero.getMockChampions()[1],
                Hero.getMockHeroes()[2].id ?: EMPTY_OBJECT_ID, Hero.getMockHeroes()[2],
                AppUtils.parseDate("16.10.2019").timeInMillis),
            Training(2, Hero.getMockChampions()[0].id ?: EMPTY_OBJECT_ID, Hero.getMockChampions()[0],
                Hero.getMockHeroes()[1].id ?: EMPTY_OBJECT_ID, Hero.getMockHeroes()[1],
                AppUtils.parseDate("17.10.2019").timeInMillis),
            Training(3, Hero.getMockChampions()[0].id ?: EMPTY_OBJECT_ID, Hero.getMockChampions()[0],
                Hero.getMockHeroes()[0].id ?: EMPTY_OBJECT_ID, Hero.getMockHeroes()[0],
                AppUtils.parseDate("18.10.2019").timeInMillis))
    }
}