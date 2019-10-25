package com.devtau.ff.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.devtau.ff.util.Constants.EMPTY_OBJECT_ID

@Entity(
    tableName = "Trainings",
    indices = [Index("trainerId", "clientId")],
    ignoredColumns = ["trainer", "client"]
)
data class Training(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "_id")
    var id: Long = EMPTY_OBJECT_ID,
    var trainerId: Long,
    var trainer: Trainer? = null,
    var clientId: Long,
    var client: Client? = null,
    var date: String
) {

    constructor(id: Long, trainerId: Long, clientId: Long, date: String): this(id, trainerId, null, clientId, null, date)

    fun isEmpty() = id == EMPTY_OBJECT_ID


    companion object {
        fun getMock() = listOf(
            Training(1, Trainer.getMock()[1].id, Trainer.getMock()[1],
                Client.getMock()[2].id, Client.getMock()[2], "16.10.2019"),
            Training(2, Trainer.getMock()[0].id, Trainer.getMock()[0],
                Client.getMock()[1].id, Client.getMock()[1], "17.10.2019"),
            Training(3, Trainer.getMock()[0].id, Trainer.getMock()[0],
                Client.getMock()[0].id, Client.getMock()[0], "18.10.2019"))
    }
}