package com.devtau.ironHeroes.data.model

import androidx.room.Entity
import androidx.room.Index
import com.devtau.ironHeroes.util.Constants.EMPTY_OBJECT_ID

@Entity(
    tableName = "Trainings",
    indices = [Index("championId", "heroId")],
    ignoredColumns = ["champion", "hero"]
)
class Training(
    id: Long?,
    var championId: Long,
    var champion: Champion? = null,
    var heroId: Long,
    var hero: Hero? = null,
    var date: String
): DataObject(id) {

    constructor(id: Long?, championId: Long, heroId: Long, date: String): this(id, championId, null, heroId, null, date)


    companion object {
        fun getMock() = listOf(
            Training(1, Champion.getMock()[1].id ?: EMPTY_OBJECT_ID, Champion.getMock()[1],
                Hero.getMock()[2].id ?: EMPTY_OBJECT_ID, Hero.getMock()[2], "16.10.2019"),
            Training(2, Champion.getMock()[0].id ?: EMPTY_OBJECT_ID, Champion.getMock()[0],
                Hero.getMock()[1].id ?: EMPTY_OBJECT_ID, Hero.getMock()[1], "17.10.2019"),
            Training(3, Champion.getMock()[0].id ?: EMPTY_OBJECT_ID, Champion.getMock()[0],
                Hero.getMock()[0].id ?: EMPTY_OBJECT_ID, Hero.getMock()[0], "18.10.2019"))
    }
}