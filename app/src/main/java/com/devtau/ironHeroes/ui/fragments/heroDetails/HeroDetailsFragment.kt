package com.devtau.ironHeroes.ui.fragments.heroDetails

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import com.devtau.ironHeroes.IronHeroesApp
import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.data.model.Hero
import com.devtau.ironHeroes.enums.Gender
import com.devtau.ironHeroes.enums.HumanType
import com.devtau.ironHeroes.ui.DependencyRegistry
import com.devtau.ironHeroes.ui.fragments.BaseFragment
import com.devtau.ironHeroes.ui.fragments.initActionBar
import com.devtau.ironHeroes.util.*
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.fragment_hero_details.*
import java.util.*

class HeroDetailsFragment: BaseFragment(),
    HeroDetailsContract.View {

    private lateinit var presenter: HeroDetailsContract.Presenter
    private var newHero: Boolean = false
    private var listener: Listener? = null


    //<editor-fold desc="Framework overrides">
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Listener) listener = context
        else throw RuntimeException("$context must implement $LOG_TAG Listener")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DependencyRegistry.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_hero_details, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    override fun onStart() {
        super.onStart()
        presenter.restartLoaders()
        initInputSubscriptions()
    }

    override fun onStop() {
        super.onStop()
        presenter.onStop()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PermissionHelperImpl.CALL_REQUEST_CODE &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            callHero()
        }
    }
    //</editor-fold>


    //<editor-fold desc="Interface overrides">
    override fun getLogTag() = LOG_TAG
    override fun initActionbar() = false

    override fun showScreenTitle(newHero: Boolean, humanType: HumanType) {
        this.newHero = newHero
        activity?.initActionBar(when (humanType) {
            HumanType.HERO -> if (newHero) R.string.hero_add else R.string.hero_edit
            HumanType.CHAMPION -> if (newHero) R.string.champion_add else R.string.champion_edit
        })
    }

    override fun showBirthdayNA() {
        birthdayText?.text = getString(R.string.not_filled)
    }

    override fun showHeroDetails(hero: Hero?) {
        Logger.d(LOG_TAG, "showHeroDetails. hero=$hero")
        AppUtils.updateInputField(firstNameInput, hero?.firstName)
        AppUtils.updateInputField(secondNameInput, hero?.secondName)
        AppUtils.updateInputField(phoneInput, hero?.phone)

        genderFemale?.isChecked = hero?.gender == Gender.FEMALE.code
        genderMale?.isChecked = hero?.gender == Gender.MALE.code

        AppUtils.updateInputField(vkIdInput, hero?.vkId)
        AppUtils.updateInputField(emailInput, hero?.email)
        AppUtils.updateInputField(birthdayText, DateUtils.formatDate(hero?.birthDay))
        isChampion?.isChecked = hero?.humanType == HumanType.CHAMPION
    }

    override fun onDateSet(date: Calendar) {
        birthdayText?.text = DateUtils.formatDate(date)
        updateHeroData("birthdayText", birthdayText?.text?.toString())
    }

    override fun showDeleteHeroBtn(show: Boolean) {
        fab?.postDelayed({ if (show) fab.show() else fab.hide() }, Constants.STANDARD_DELAY_MS)
        fab.setOnClickListener { presenter.deleteHero() }
    }

    override fun showHumanType(humanType: HumanType) {
        isChampion?.isChecked = humanType == HumanType.CHAMPION
    }

    override fun closeScreen() {
        view?.let {
            val controller = Navigation.findNavController(it)
            controller.popBackStack(R.id.heroesFragment, false)
        }
    }

    override fun onBackPressed(action: Action) = presenter.onBackPressed(action)

    override fun updateHeroData(field: String, value: String?) {
        Logger.d(LOG_TAG, "updateHeroData. new data in $field detected. value=$value")
        val humanType = if (isChampion?.isChecked == true) HumanType.CHAMPION else HumanType.HERO
        val gender = when {
            genderFemale?.isChecked == true -> Gender.FEMALE.code
            genderMale?.isChecked == true -> Gender.MALE.code
            else -> null
        }
        presenter.updateHeroData(
            humanType,
            firstName = firstNameInput?.text?.toString(),
            secondName = secondNameInput?.text?.toString(),
            phone = AppUtils.clearPhoneFromMask(phoneInput?.text?.toString()),
            gender = gender,

            vkId = vkIdInput?.text?.toString(),
            email = emailInput?.text?.toString(),
            birthDay = birthdayText?.text?.toString(),
            avatarUrl = listener?.provideAvatarUrl(),
            avatarId = null
        )
    }
    //</editor-fold>


    fun configureWith(presenter: HeroDetailsContract.Presenter) {
        this.presenter = presenter
    }


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
        birthdayText?.setOnClickListener { presenter.showBirthDayDialog(context, birthdayText?.text?.toString()) }
        useVkAvatar?.setOnClickListener {
            val activity = activity as AppCompatActivity? ?: return@setOnClickListener
            IronHeroesApp.loginVK(activity)
        }
        isChampion?.setOnCheckedChangeListener { _, isChecked ->
            updateHeroData("isChampion", isChecked.toString())
            showScreenTitle(newHero, if (isChecked) HumanType.CHAMPION else HumanType.HERO)
        }
    }

    private fun initInputSubscriptions() {
        subscribeField(firstNameInput, Consumer { updateHeroData("firstNameInput", it) })
        subscribeField(secondNameInput, Consumer { updateHeroData("secondNameInput", it) })
        subscribeField(phoneInput, Consumer { updateHeroData("phoneInput", it) })
        subscribeField(vkIdInput, Consumer { updateHeroData("vkIdInput", it) })
        subscribeField(emailInput, Consumer { updateHeroData("emailInput", it) })
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
        val activity = activity as AppCompatActivity? ?: return
        if (!permissionHelper.checkCallPermission(activity)) {
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


    interface Listener {
        fun provideAvatarUrl(): String?
    }


    companion object {
        private const val LOG_TAG = "HeroDetailsActivity"
    }
}