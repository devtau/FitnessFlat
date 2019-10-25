package com.devtau.ff.ui.activities.clientDetails

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import android.widget.TextView
import com.devtau.ff.FFApplication
import com.devtau.ff.R
import com.devtau.ff.enums.Gender
import com.devtau.ff.data.model.Client
import com.devtau.ff.ui.DependencyRegistry
import com.devtau.ff.ui.activities.ViewSubscriberActivity
import com.devtau.ff.util.*
import com.vk.sdk.VKSdk
import com.vk.sdk.api.VKApiConst
import com.vk.sdk.api.VKParameters
import com.vk.sdk.api.VKRequest
import com.vk.sdk.api.VKResponse
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.activity_client_details.*
import java.util.*

class ClientDetailsActivity: ViewSubscriberActivity(),
    ClientDetailsView {

    var presenter: ClientDetailsPresenter? = null
    var avatarUrl: String? = null


    //<editor-fold desc="Framework overrides">
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_details)
        DependencyRegistry().inject(this)
        initUi()
    }

    override fun onStart() {
        super.onStart()
        presenter?.restartLoaders()
        initInputSubscriptions()
    }

    override fun onStop() {
        super.onStop()
        presenter?.onStop()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PermissionHelperImpl.CALL_REQUEST_CODE &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            callClient()
        }
    }

    override fun onBackPressed() {
        presenter?.onBackPressed(Action { super.onBackPressed() })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        val authListener = FFApplication.getVKAuthListener(this, PreferencesManager.getInstance(this))
        if (VKSdk.onActivityResult(requestCode, resultCode, intent, authListener)) {
            val params = VKParameters()
            params[VKApiConst.FIELDS] = "photo_max_orig"

            val request = VKRequest("users.get", params)
            request.executeWithListener(object: VKRequest.VKRequestListener() {
                override fun onComplete(response: VKResponse?) {
                    super.onComplete(response)
                    val resp = response?.json?.getJSONArray("response")
                    val user = resp?.getJSONObject(0)
                    avatarUrl = user?.getString("photo_max_orig")
                    updateClientData("avatar", avatarUrl)
                }
            })
        } else super.onActivityResult(requestCode, resultCode, intent)
    }
    //</editor-fold>


    //<editor-fold desc="View overrides">
    override fun showMsg(msgId: Int, confirmedListener: Action?) = showMsg(getString(msgId), confirmedListener)
    override fun showMsg(msg: String, confirmedListener: Action?) = AppUtils.alertD(LOG_TAG, msg, this, confirmedListener)

    override fun showScreenTitle(newClient: Boolean) {
        val toolbarTitle = if (newClient) R.string.client_add else R.string.client_edit
        AppUtils.initToolbar(this, toolbarTitle, true)
    }

    override fun showBirthdayNA() {
        birthdayText?.text = getString(R.string.not_filled)
    }

    override fun showClientDetails(client: Client?) {
        fun updateInputField(input: TextView?, value: String?) {
            if (input != null && input.text?.toString() != value) {
                input.setText(value)
                if (input is EditText) input.setSelection(value?.length ?: 0)
            }
        }

        Logger.d(LOG_TAG, "showClientDetails. client=$client")
        updateInputField(firstNameInput, client?.firstName)
        updateInputField(secondNameInput, client?.secondName)
        updateInputField(phoneInput, client?.phone)

        genderFemale?.isChecked = client?.gender == Gender.FEMALE.code
        genderMale?.isChecked = client?.gender == Gender.MALE.code

        updateInputField(vkIdInput, client?.vkId)
        updateInputField(emailInput, client?.email)
        updateInputField(birthdayText, client?.birthDay)
    }

    override fun onDateSet(date: Calendar) {
        birthdayText?.text = AppUtils.formatBirthday(date)
        updateClientData("birthdayText", birthdayText?.text?.toString())
    }

    override fun showDeleteClientBtn(show: Boolean) {
        fab?.postDelayed({ if (show) fab.show() else fab.hide() }, Constants.STANDARD_DELAY_MS)
        fab.setOnClickListener { presenter?.deleteClient() }
    }

    override fun closeScreen() = finish()
    //</editor-fold>


    //<editor-fold desc="Private methods">
    private fun initUi() {
        genderFemale?.setOnClickListener {
            genderFemale?.isChecked = true
            genderMale?.isChecked = false
            updateClientData("genderInput", Gender.FEMALE.code)
        }
        genderMale?.setOnClickListener {
            genderMale?.isChecked = true
            genderFemale?.isChecked = false
            updateClientData("genderInput", Gender.MALE.code)
        }
        phoneText?.setOnClickListener { callClient() }
        AppUtils.initPhoneRMR(phoneInput)
        vkText?.setOnClickListener { openVk(vkIdInput?.text?.toString()) }
        emailText?.setOnClickListener { composeEmail(emailInput?.text?.toString()) }
        birthdayInput?.setOnClickListener { presenter?.showBirthDayDialog(this, birthdayText?.text?.toString()) }
        useVkAvatar?.setOnClickListener { FFApplication.loginVK(this) }
    }

    private fun initInputSubscriptions() {
        subscribeField(firstNameInput, Consumer { updateClientData("firstNameInput", it) })
        subscribeField(secondNameInput, Consumer { updateClientData("secondNameInput", it) })
        subscribeField(phoneInput, Consumer { updateClientData("phoneInput", it) })
        subscribeField(vkIdInput, Consumer { updateClientData("vkIdInput", it) })
        subscribeField(emailInput, Consumer { updateClientData("emailInput", it) })
    }

    private fun updateClientData(field: String, value: String?) {
        Logger.d(LOG_TAG, "updateClientData. new data in $field detected. value=$value")
        val gender = when {
            genderFemale?.isChecked == true -> Gender.FEMALE.code
            genderMale?.isChecked == true -> Gender.MALE.code
            else -> null
        }
        presenter?.updateClientData(
            firstName = firstNameInput?.text?.toString(),
            secondName = secondNameInput?.text?.toString(),
            phone = AppUtils.clearPhoneFromMask(phoneInput?.text?.toString()),
            gender = gender,

            vkId = vkIdInput?.text?.toString(),
            email = emailInput?.text?.toString(),
            birthDay = birthdayText?.text?.toString(),
            avatarUrl = avatarUrl,
            avatarId = null
        )
    }

    private fun callClient() {
        val clearedPhone = AppUtils.clearPhoneFromMask(phoneInput?.text?.toString())
        if (TextUtils.isEmpty(clearedPhone) || clearedPhone.length != Constants.UNMASKED_PHONE_LENGTH) {
            Logger.d(LOG_TAG, "callClient. phone is incorrect. aborting")
            showMsg(R.string.phone_empty)
            return
        }
        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:$clearedPhone")
        val permissionHelper = PermissionHelperImpl()
        if (!permissionHelper.checkCallPermission(this)) {
            permissionHelper.requestCallPermission(this)
            return
        }
        startActivity(intent)
    }

    private fun openVk(vkId: String?) {
        if (TextUtils.isEmpty(vkId)) {
            Logger.d(LOG_TAG, "openVk. vkId is empty. aborting")
            showMsg(R.string.vk_id_empty)
            return
        }
        val formatter = getString(R.string.vk_id_formatter)
        val url = String.format(Locale.getDefault(), formatter, vkId)
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    private fun composeEmail(emailAddress: String?) {
        if (TextUtils.isEmpty(emailAddress)) {
            Logger.d(LOG_TAG, "composeEmail. email is empty. aborting")
            showMsg(R.string.email_empty)
            return
        }
        startActivity(Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", emailAddress, null)))
    }
    //</editor-fold>


    companion object {
        const val CLIENT_ID = "clientId"
        private const val LOG_TAG = "ClientDetailsActivity"

        fun newInstance(context: Context, clientId: Long?) {
            val intent = Intent(context, ClientDetailsActivity::class.java)
            if (clientId != null) intent.putExtra(CLIENT_ID, clientId)
            context.startActivity(intent)
        }
    }
}