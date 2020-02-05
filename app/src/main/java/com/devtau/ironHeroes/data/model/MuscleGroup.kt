package com.devtau.ironHeroes.data.model

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.devtau.ironHeroes.R
import java.util.*

@Entity(tableName = "MuscleGroups")
class MuscleGroup(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "muscleGroupId")
    var id: Long?,
    var name: String
) {

    override fun equals(other: Any?): Boolean = when {
        other !is MuscleGroup -> false
        other.id != this.id -> false
        else -> true
    }

    override fun hashCode(): Int = Objects.hash(id)

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