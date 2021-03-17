package com.devtau.ironHeroes.data.model

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.devtau.ironHeroes.util.DateUtils
import java.util.*

@Entity(
    tableName = "Trainings",
    indices = [Index("championId", "heroId")],
    ignoredColumns = ["champion", "hero", "exercises"]
)
data class Training(
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

    override fun toString() = "id=$id, championId=$championId, heroId=$heroId, " +
            "date=${DateUtils.formatDateTimeWithWeekDay(date)}, exercises size=${exercises?.size}"

    fun someFieldsChanged(championId: Long?, heroId: Long?, date: Long?) =
        championId != this.championId
                || heroId != this.heroId
                || date != this.date

    fun getDateCal(): Calendar {
        val cal = Calendar.getInstance()
        cal.timeInMillis = date
        return cal
    }

    fun formatDateTimeWithWeekDay() = DateUtils.formatDateTimeWithWeekDay(date)


    companion object {
        fun allObligatoryPartsPresent(championId: Long?, heroId: Long?, date: Long?) =
            championId != null && heroId != null && date != null

        fun getMock(c: Context): List<Training> {
            val roma = Hero.getMockChampions(c)[0]
            val anton = Hero.getMockChampions(c)[1]
            val denis = Hero.getMockHeroes(c)[0]

            return listOf(
                Training(1, anton.id, denis.id, DateUtils.parseDateTime("25.01.2021 8:00").timeInMillis),
                Training(2, roma.id, denis.id, DateUtils.parseDateTime("27.01.2021 9:00").timeInMillis),
                Training(3, roma.id, denis.id, DateUtils.parseDateTime("29.01.2021 8:30").timeInMillis)
            )
        }
    }
}