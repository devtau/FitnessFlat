package com.devtau.ff.ui.activities.clientDetails

import android.app.DatePickerDialog
import android.content.Context
import com.devtau.ff.R
import com.devtau.ff.db.DataLayer
import com.devtau.ff.rest.NetworkLayer
import com.devtau.ff.rest.model.Client
import com.devtau.ff.ui.DBSubscriber
import com.devtau.ff.util.Constants
import com.devtau.ff.util.Logger
import com.devtau.ff.util.PreferencesManager
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import java.util.*

class ClientDetailsPresenterImpl(
    private val view: ClientDetailsView,
    private val dataLayer: DataLayer,
    private val networkLayer: NetworkLayer,
    private val prefs: PreferencesManager?,
    private val clientId: Long?
): DBSubscriber(), ClientDetailsPresenter {

    private var client: Client? = null


    //<editor-fold desc="Presenter overrides">
    override fun restartLoaders() {
        if (clientId == null) {
            view.showScreenTitle(true)
            view.showBirthdayNA()
            view.showDeleteClientBtn(false)
        } else {
            dataLayer.getClientByIdAndClose(clientId, Consumer {
                client = it
                view.showClientDetails(client)
                view.showScreenTitle(client == null)
                view.showDeleteClientBtn(client != null)
            })
        }
    }

    override fun updateClientData(firstName: String?, secondName: String?, phone: String?, gender: String?,
                                  vkId: String?, email: String?, birthDay: String?,
                                  avatarUrl: String?, avatarId: Int?) {
        val allPartsPresent = Client.allObligatoryPartsPresent(firstName, secondName, phone, gender)
        val someFieldsChanged = client?.someFieldsChanged(firstName, secondName, phone, gender, vkId, email, birthDay,
            avatarUrl, avatarId) ?: true
        Logger.d(LOG_TAG, "updateClientData. allPartsPresent=$allPartsPresent, someFieldsChanged=$someFieldsChanged")
        if (allPartsPresent && someFieldsChanged) {
            val clientId = if (client == null) Constants.OBJECT_ID_NA else client!!.id
            client = Client(clientId, firstName!!, secondName!!, phone!!, gender!!, vkId, email, birthDay,
                avatarUrl, avatarId ?: client?.avatarId)
            dataLayer.updateClient(client)
        }
    }

    override fun showBirthDayDialog(context: Context, selectedBirthday: String?) {
        val nowMinusCentury = Calendar.getInstance()
        nowMinusCentury.add(Calendar.YEAR, -100)
        val birthDay = Client.parseBirthday(client?.birthDay ?: selectedBirthday)

        val dialog = DatePickerDialog(context,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth -> onDateSet(year, month, dayOfMonth) },
            birthDay.get(Calendar.YEAR), birthDay.get(Calendar.MONTH), birthDay.get(Calendar.DAY_OF_MONTH))
        dialog.datePicker.minDate = nowMinusCentury.timeInMillis
        dialog.datePicker.maxDate = Calendar.getInstance().timeInMillis
        dialog.show()
    }

    override fun onBackPressed(action: Action) {
        if (client == null) {
            view.showMsg(R.string.not_finished, action)
        } else {
            action.run()
        }
    }

    override fun deleteClient() {
        view.showMsg(R.string.confirm_delete, Action {
            dataLayer.deleteClient(client)
            view.closeScreen()
        })
    }
    //</editor-fold>


    private fun onDateSet(year: Int, month: Int, dayOfMonth: Int) {
        val date = Calendar.getInstance()
        date.set(Calendar.YEAR, year)
        date.set(Calendar.MONTH, month)
        date.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        date.set(Calendar.HOUR_OF_DAY, 0)
        date.set(Calendar.MINUTE, 0)
        date.set(Calendar.SECOND, 0)

        view.onDateSet(date)
    }


    companion object {
        private const val LOG_TAG = "ClientDetailsPresenter"
    }
}