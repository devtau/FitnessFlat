package com.devtau.ff.ui.activities.clientDetails

import com.devtau.ff.data.model.Client
import com.devtau.ff.ui.StandardView
import java.util.*

interface ClientDetailsView: StandardView {
    fun showScreenTitle(newClient: Boolean)
    fun showBirthdayNA()
    fun showClientDetails(client: Client?)
    fun onDateSet(date: Calendar)
    fun showDeleteClientBtn(show: Boolean)
    fun closeScreen()
}