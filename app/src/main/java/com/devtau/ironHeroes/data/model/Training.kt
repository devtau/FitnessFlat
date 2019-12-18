package com.devtau.ironHeroes.data.model

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.devtau.ironHeroes.util.AppUtils
import java.util.*

@Entity(
    tableName = "Trainings",
    indices = [Index("championId", "heroId")],
    ignoredColumns = ["champion", "hero", "exercises"]
)
class Training(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "trainingId")
    var id: Long?,
    var championId: Long?,
    var champion: Hero?,
    var heroId: Long?,
    var hero: Hero?,
    var date: Long,
    var exercises: List<ExerciseInTraining>? = null
) {

    constructor(id: Long?, championId: Long?, heroId: Long?, date: Long):
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

        fun getMock(c: Context): List<Training> {
            val roma = Hero.getMockChampions(c)[0]
            val anton = Hero.getMockChampions(c)[1]
            val denis = Hero.getMockHeroes(c)[0]

            return listOf(
                Training(1, anton.id, denis.id, AppUtils.parseDateTime("8.10.2019 8:00").timeInMillis),
                Training(2, roma.id, denis.id, AppUtils.parseDateTime("9.10.2019 9:00").timeInMillis),
                Training(3, roma.id, denis.id, AppUtils.parseDateTime("11.10.2019 9:00").timeInMillis),

                Training(4, anton.id, denis.id, AppUtils.parseDateTime("14.10.2019 9:00").timeInMillis),
                Training(5, anton.id, denis.id, AppUtils.parseDateTime("15.10.2019 9:00").timeInMillis),
                Training(6, anton.id, denis.id, AppUtils.parseDateTime("17.10.2019 9:00").timeInMillis),

                Training(7, roma.id, denis.id, AppUtils.parseDateTime("21.10.2019 9:00").timeInMillis),
                Training(8, roma.id, denis.id, AppUtils.parseDateTime("22.10.2019 9:00").timeInMillis),
                Training(9, anton.id, denis.id, AppUtils.parseDateTime("25.10.2019 9:00").timeInMillis),

                Training(10, roma.id, denis.id, AppUtils.parseDateTime("28.10.2019 9:00").timeInMillis),
                Training(11, roma.id, denis.id, AppUtils.parseDateTime("29.10.2019 9:00").timeInMillis),
                Training(12, anton.id, denis.id, AppUtils.parseDateTime("01.11.2019 9:00").timeInMillis),

                Training(13, anton.id, denis.id, AppUtils.parseDateTime("04.11.2019 10:00").timeInMillis),
                Training(14, anton.id, denis.id, AppUtils.parseDateTime("05.11.2019 9:00").timeInMillis),
                Training(15, roma.id, denis.id, AppUtils.parseDateTime("08.11.2019 9:00").timeInMillis),

                Training(16, anton.id, denis.id, AppUtils.parseDateTime("11.11.2019 10:00").timeInMillis),
                Training(17, roma.id, denis.id, AppUtils.parseDateTime("14.11.2019 9:00").timeInMillis),
                Training(18, roma.id, denis.id, AppUtils.parseDateTime("15.11.2019 9:00").timeInMillis),

                Training(19, anton.id, denis.id, AppUtils.parseDateTime("18.11.2019 9:00").timeInMillis),
                Training(20, roma.id, denis.id, AppUtils.parseDateTime("21.11.2019 9:00").timeInMillis),
                Training(21, anton.id, denis.id, AppUtils.parseDateTime("22.11.2019 9:00").timeInMillis),


                Training(101, roma.id, Hero.getMockHeroes(c)[1].id, AppUtils.parseDateTime("21.10.2019 9:30").timeInMillis),
                Training(102, roma.id, Hero.getMockHeroes(c)[2].id, AppUtils.parseDateTime("21.10.2019 10:00").timeInMillis)
            )
        }
    }
}