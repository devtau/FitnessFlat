package com.devtau.ironHeroes.data.model

import androidx.room.Entity
import androidx.room.Index
import com.devtau.ironHeroes.util.AppUtils
import com.devtau.ironHeroes.util.Constants.EMPTY_OBJECT_ID
import java.util.*

@Entity(
    tableName = "Trainings",
    indices = [Index("championId", "heroId")],
    ignoredColumns = ["champion", "hero", "exercises"]
)
class Training(
    id: Long?,
    var championId: Long,
    var champion: Hero?,
    var heroId: Long,
    var hero: Hero?,
    var date: Long,
    var exercises: List<ExerciseInTraining>?
): DataObject(id) {

    constructor(id: Long?, championId: Long, heroId: Long, date: Long):
            this(id, championId, null, heroId, null, date, null)


    fun someFieldsChanged(championId: Long?, heroId: Long?, date: Long?) =
        championId != this.championId
                || heroId != this.heroId
                || date != this.date

    override fun toString(): String =
        "championId=$championId, heroId=$heroId, date=${AppUtils.formatDate(date)}"

    fun getDateCal(): Calendar {
        val cal = Calendar.getInstance()
        cal.timeInMillis = date
        return cal
    }


    companion object {
        fun allObligatoryPartsPresent(championId: Long?, heroId: Long?, date: Long?) =
            championId != null && heroId != null && date != null

        fun getMock(): List<Training> {
            val roma = Hero.getMockChampions()[0]
            val anton = Hero.getMockChampions()[1]
            val denis = Hero.getMockHeroes()[0]

            return listOf(
                Training(1, anton.id ?: EMPTY_OBJECT_ID, anton, denis.id ?: EMPTY_OBJECT_ID, denis,
                    AppUtils.parseDateTime("8.10.2019 8:00").timeInMillis, null),
                Training(2, roma.id ?: EMPTY_OBJECT_ID, roma, denis.id ?: EMPTY_OBJECT_ID, denis,
                    AppUtils.parseDateTime("9.10.2019 9:00").timeInMillis, null),
                Training(3, roma.id ?: EMPTY_OBJECT_ID, roma, denis.id ?: EMPTY_OBJECT_ID, denis,
                    AppUtils.parseDateTime("11.10.2019 9:00").timeInMillis, null),

                Training(4, anton.id ?: EMPTY_OBJECT_ID, anton, denis.id ?: EMPTY_OBJECT_ID, denis,
                    AppUtils.parseDateTime("14.10.2019 9:00").timeInMillis, null),
                Training(5, anton.id ?: EMPTY_OBJECT_ID, anton, denis.id ?: EMPTY_OBJECT_ID, denis,
                    AppUtils.parseDateTime("15.10.2019 9:00").timeInMillis, null),
                Training(6, anton.id ?: EMPTY_OBJECT_ID, anton, denis.id ?: EMPTY_OBJECT_ID, denis,
                    AppUtils.parseDateTime("17.10.2019 9:00").timeInMillis, null),

                Training(7, roma.id ?: EMPTY_OBJECT_ID, roma, denis.id ?: EMPTY_OBJECT_ID, denis,
                    AppUtils.parseDateTime("21.10.2019 9:00").timeInMillis, null),
                Training(8, roma.id ?: EMPTY_OBJECT_ID, roma, denis.id ?: EMPTY_OBJECT_ID, denis,
                    AppUtils.parseDateTime("22.10.2019 9:00").timeInMillis, null),
                Training(9, anton.id ?: EMPTY_OBJECT_ID, anton, denis.id ?: EMPTY_OBJECT_ID, denis,
                    AppUtils.parseDateTime("25.10.2019 9:00").timeInMillis, null),

                Training(10, roma.id ?: EMPTY_OBJECT_ID, roma, denis.id ?: EMPTY_OBJECT_ID, denis,
                    AppUtils.parseDateTime("28.10.2019 9:00").timeInMillis, null),
                Training(11, roma.id ?: EMPTY_OBJECT_ID, roma, denis.id ?: EMPTY_OBJECT_ID, denis,
                    AppUtils.parseDateTime("29.10.2019 9:00").timeInMillis, null),
                Training(12, anton.id ?: EMPTY_OBJECT_ID, anton, denis.id ?: EMPTY_OBJECT_ID, denis,
                    AppUtils.parseDateTime("01.11.2019 9:00").timeInMillis, null),


                Training(13, roma.id ?: EMPTY_OBJECT_ID, roma,
                    Hero.getMockHeroes()[1].id ?: EMPTY_OBJECT_ID, Hero.getMockHeroes()[1],
                    AppUtils.parseDateTime("21.10.2019 9:30").timeInMillis, null),
                Training(14, roma.id ?: EMPTY_OBJECT_ID, roma,
                    Hero.getMockHeroes()[2].id ?: EMPTY_OBJECT_ID, Hero.getMockHeroes()[2],
                    AppUtils.parseDateTime("21.10.2019 10:00").timeInMillis, null)
            )
        }
    }
}