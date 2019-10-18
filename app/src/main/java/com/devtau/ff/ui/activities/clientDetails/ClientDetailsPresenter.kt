package com.devtau.ff.ui.activities.clientDetails

import android.content.Context
import io.reactivex.functions.Action

interface ClientDetailsPresenter {
    fun onStop()
    fun restartLoaders()
    fun updateClientData(firstName: String?, secondName: String?, phone: String?, gender: String?,
                         vkId: String?, email: String?, birthDay: String?, avatarUrl: String?, avatarId: Int?)
    fun showBirthDayDialog(context: Context, selectedBirthday: String?)
    fun onBackPressed(action: Action)
    fun deleteClient()
}