package com.devtau.ff.db.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.devtau.ff.rest.model.Client

@Entity(tableName = "Clients")
data class ClientStored(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "_id") var id: Long,
    var firstName: String,
    var secondName: String,
    var phone: String,
    var gender: String,

    var vkId: String?,
    var email: String?,
    var birthDay: String?,
    var avatarUrl: String?,
    var avatarId: Int?
) {

    constructor(client: Client): this(
        client.id, client.firstName, client.secondName, client.phone, client.gender,
        client.vkId, client.email, client.birthDay, client.avatarUrl, client.avatarId)

    fun convertToClient(): Client = Client(id, firstName, secondName, phone, gender, vkId, email, birthDay, avatarUrl, avatarId)


    companion object {
        fun convertListToClients(clientsStored: List<ClientStored>): List<Client> {
            val clients = ArrayList<Client>()
            for (next in clientsStored) clients.add(next.convertToClient())
            return clients
        }

        fun convertListToStored(clients: List<Client>): List<ClientStored> {
            val clientsStored = ArrayList<ClientStored>()
            for (next in clients) clientsStored.add(ClientStored(next))
            return clientsStored
        }
    }
}