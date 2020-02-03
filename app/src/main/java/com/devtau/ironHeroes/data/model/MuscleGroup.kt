package com.devtau.ironHeroes.data.model

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.devtau.ironHeroes.R

@Entity(tableName = "MuscleGroups")
class MuscleGroup(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "muscleGroupId")
    var id: Long?,
    var name: String
) {

    override fun toString(): String = name

    companion object {
        fun getMock(c: Context) = listOf(
            MuscleGroup(1, c.getString(R.string.chest)),
            MuscleGroup(2, c.getString(R.string.biceps)),
            MuscleGroup(3, c.getString(R.string.triceps)),
            MuscleGroup(4, c.getString(R.string.delta)),
            MuscleGroup(5, c.getString(R.string.back)),
            MuscleGroup(6, c.getString(R.string.press)),
            MuscleGroup(8, c.getString(R.string.legs))
        )
    }
}