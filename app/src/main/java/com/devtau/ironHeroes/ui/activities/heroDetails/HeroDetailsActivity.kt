package com.devtau.ironHeroes.ui.activities.heroDetails

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import android.widget.TextView
import com.devtau.ironHeroes.IronHeroesApp
import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.enums.Gender
import com.devtau.ironHeroes.data.model.Hero
import com.devtau.ironHeroes.ui.DependencyRegistry
import com.devtau.ironHeroes.ui.activities.ViewSubscriberActivity
import com.devtau.ironHeroes.util.*
import com.vk.sdk.VKSdk
import com.vk.sdk.api.VKApiConst
import com.vk.sdk.api.VKParameters
import com.vk.sdk.api.VKRequest
import com.vk.sdk.api.VKResponse
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.activity_hero_details.*
import java.util.*

class HeroDetailsActivity: ViewSubscriberActivity(),
    HeroDetailsView {

    var presenter: HeroDetailsPresenter? = null
    var avatarUrl: String? = null


    //<editor-fold desc="Framework overrides">
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hero_details)
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
            callHero()
        }
    }

    override fun onBackPressed() {
        presenter?.onBackPressed(Action { super.onBackPressed() })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        val authListener = IronHeroesApp.getVKAuthListener(this, PreferencesManager.getInstance(this))
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
                    updateHeroData("avatar", avatarUrl)
                }
            })
        } else super.onActivityResult(requestCode, resultCode, intent)
    }
    //</editor-fold>


    //<editor-fold desc="View overrides">
    override fun showMsg(msgId: Int, confirmedListener: Action?) = showMsg(getString(msgId), confirmedListener)
    override fun showMsg(msg: String, confirmedListener: Action?) = AppUtils.alertD(LOG_TAG, msg, this, confirmedListener)

    override fun showScreenTitle(newHero: Boolean) {
        val toolbarTitle = if (newHero) R.string.hero_add else R.string.hero_edit
        AppUtils.initToolbar(this, toolbarTitle, true)
    }

    override fun showBirthdayNA() {
        birthdayText?.text = getString(R.string.not_filled)
    }

    override fun showHeroDetails(hero: Hero?) {
        fun updateInputField(input: TextView?, value: String?) {
            if (input != null && input.text?.toString() != value) {
                input.setText(value)
                if (input is EditText) input.setSelection(value?.length ?: 0)
            }
        }

        Logger.d(LOG_TAG, "showHeroDetails. hero=$hero")
        updateInputField(firstNameInput, hero?.firstName)
        updateInputField(secondNameInput, hero?.secondName)
        updateInputField(phoneInput, hero?.phone)

        genderFemale?.isChecked = hero?.gender == Gender.FEMALE.code
        genderMale?.isChecked = hero?.gender == Gender.MALE.code

        updateInputField(vkIdInput, hero?.vkId)
        updateInputField(emailInput, hero?.email)
        updateInputField(birthdayText, hero?.birthDay)
    }

    override fun onDateSet(date: Calendar) {
        birthdayText?.text = AppUtils.formatBirthday(date)
        updateHeroData("birthdayText", birthdayText?.text?.toString())
    }

    override fun showDeleteHeroBtn(show: Boolean) {
        fab?.postDelayed({ if (show) fab.show() else fab.hide() }, Constants.STANDARD_DELAY_MS)
        fab.setOnClickListener { presenter?.deleteHero() }
    }

    override fun closeScreen() = finish()
    //</editor-fold>


    //<editor-fold desc="Private methods">
    private fun initUi() {
        genderFemale?.setOnClickListener {
            genderFemale?.isChecked = true
            genderMale?.isChecked = false
            updateHeroData("genderInput", Gender.FEMALE.code)
        }
        genderMale?.setOnClickListener {
            genderMale?.isChecked = true
            genderFemale?.isChecked = false
            updateHeroData("genderInput", Gender.MALE.code)
        }
        phoneText?.setOnClickListener { callHero() }
        AppUtils.initPhoneRMR(phoneInput)
        vkText?.setOnClickListener { openVk(vkIdInput?.text?.toString()) }
        emailText?.setOnClickListener { composeEmail(emailInput?.text?.toString()) }
        birthdayInput?.setOnClickListener { presenter?.showBirthDayDialog(this, birthdayText?.text?.toString()) }
        useVkAvatar?.setOnClickListener { IronHeroesApp.loginVK(this) }
    }

    private fun initInputSubscriptions() {
        subscribeField(firstNameInput, Consumer { updateHeroData("firstNameInput", it) })
        subscribeField(secondNameInput, Consumer { updateHeroData("secondNameInput", it) })
        subscribeField(phoneInput, Consumer { updateHeroData("phoneInput", it) })
        subscribeField(vkIdInput, Consumer { updateHeroData("vkIdInput", it) })
        subscribeField(emailInput, Consumer { updateHeroData("emailInput", it) })
    }

    private fun updateHeroData(field: String, value: String?) {
        Logger.d(LOG_TAG, "updateHeroData. new data in $field detected. value=$value")
        val gender = when {
            genderFemale?.isChecked == true -> Gender.FEMALE.code
            genderMale?.isChecked == true -> Gender.MALE.code
            else -> null
        }
        presenter?.updateHeroData(
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

    private fun callHero() {
        val clearedPhone = AppUtils.clearPhoneFromMask(phoneInput?.text?.toString())
        if (TextUtils.isEmpty(clearedPhone) || clearedPhone.length != Constants.UNMASKED_PHONE_LENGTH) {
            Logger.d(LOG_TAG, "callHero. phone is incorrect. aborting")
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
        const val HERO_ID = "heroId"
        private const val LOG_TAG = "HeroDetailsActivity"

        fun newInstance(context: Context, heroId: Long?) {
            val intent = Intent(context, HeroDetailsActivity::class.java)
            if (heroId != null) intent.putExtra(HERO_ID, heroId)
            context.startActivity(intent)
        }
    }
}