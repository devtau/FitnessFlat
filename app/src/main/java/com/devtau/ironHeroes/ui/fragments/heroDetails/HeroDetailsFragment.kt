package com.devtau.ironHeroes.ui.fragments.heroDetails

import android.app.DatePickerDialog
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
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.devtau.ironHeroes.IronHeroesApp
import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.data.model.wrappers.DatePickerDialogDataWrapper
import com.devtau.ironHeroes.databinding.FragmentHeroDetailsBinding
import com.devtau.ironHeroes.ui.fragments.BaseFragment
import com.devtau.ironHeroes.ui.fragments.getViewModelFactory
import com.devtau.ironHeroes.ui.fragments.initActionBar
import com.devtau.ironHeroes.util.*
import io.reactivex.functions.Action
import java.util.*

class HeroDetailsFragment: BaseFragment() {

    private val _viewModel by viewModels<HeroDetailsViewModel> { getViewModelFactory() }
    private var pendingPhone: String = ""
    private var listener: Listener? = null


    //<editor-fold desc="Framework overrides">
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Listener) listener = context

        else throw RuntimeException("$context must implement $LOG_TAG Listener")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentHeroDetailsBinding.inflate(inflater, container, false).apply {
            viewModel = _viewModel
            lifecycleOwner = viewLifecycleOwner
            _viewModel.subscribeToVM()

            AppUtils.initPhoneRMR(phoneInput)
        }
        return binding.root
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PermissionHelperImpl.CALL_REQUEST_CODE &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            callHero(pendingPhone)
        }
    }
    //</editor-fold>


    fun tryCloseScreen() = _viewModel.tryCloseScreen()


    //<editor-fold desc="Private methods">
    private fun HeroDetailsViewModel.subscribeToVM() {
        view?.setupSnackbar(viewLifecycleOwner, snackbarText)

        toolbarTitle.observe(viewLifecycleOwner, EventObserver {
            activity?.initActionBar(it)
        })

        showBirthDayDialog.observe(viewLifecycleOwner, EventObserver {
            showDateDialog(it)
        })

        callHero.observe(viewLifecycleOwner, EventObserver {
            callHero(it)
        })

        openVk.observe(viewLifecycleOwner, EventObserver {
            val formatter = getString(R.string.vk_id_formatter)
            val url = String.format(Locale.getDefault(), formatter, it)
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        })

        composeEmail.observe(viewLifecycleOwner, EventObserver {
            startActivity(Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", it, null)))
        })

        useVkAvatar.observe(viewLifecycleOwner, EventObserver {
            IronHeroesApp.loginVK(activity as AppCompatActivity?)
        })

        closeScreenValidated.observe(viewLifecycleOwner, EventObserver {
            closeScreenValidated()
        })

        confirmExit.observe(viewLifecycleOwner, EventObserver {
            view?.showDialog(LOG_TAG, it, Action { closeScreenValidated() }, Action {/*NOP*/})
        })

        confirmDeleteHero.observe(viewLifecycleOwner, EventObserver {
            view?.showDialog(LOG_TAG, R.string.confirm_delete, Action {
                deleteHeroConfirmed()
            })
        })

        initInputSubscriptions()
    }

    private fun HeroDetailsViewModel.initInputSubscriptions() {
        val observer = Observer<Any> {
            updateHero(listener?.provideAvatarUrl(), null)
        }
        firstName.observe(viewLifecycleOwner, observer)
        secondName.observe(viewLifecycleOwner, observer)
        phone.observe(viewLifecycleOwner, observer)
        vkId.observe(viewLifecycleOwner, observer)
        email.observe(viewLifecycleOwner, observer)
        isChampion.observe(viewLifecycleOwner, observer)
        genderMaleChecked.observe(viewLifecycleOwner, observer)
        formattedBirthday.observe(viewLifecycleOwner, observer)
    }

    private fun showDateDialog(wrapper: DatePickerDialogDataWrapper) {
        val context = context ?: return
        val dialog = DatePickerDialog(context,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth -> onDateSet(year, month, dayOfMonth) },
            wrapper.selectedDate.get(Calendar.YEAR), wrapper.selectedDate.get(Calendar.MONTH), wrapper.selectedDate.get(Calendar.DAY_OF_MONTH))
        dialog.datePicker.minDate = wrapper.minDate.timeInMillis
        dialog.datePicker.maxDate = wrapper.maxDate.timeInMillis
        dialog.show()
    }

    private fun onDateSet(year: Int, month: Int, dayOfMonth: Int) {
        _viewModel.updateBirthday(year, month, dayOfMonth)
    }

    private fun callHero(phone: String) {
        val clearedPhone = AppUtils.clearPhoneFromMask(phone)
        if (TextUtils.isEmpty(clearedPhone) || clearedPhone.length != Constants.UNMASKED_PHONE_LENGTH) {
            Logger.d(LOG_TAG, "callHero. phone is incorrect. aborting")
            view?.showDialog(LOG_TAG, R.string.phone_empty)
            return
        }
        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:$clearedPhone")
        val permissionHelper = PermissionHelperImpl()
        val activity = activity as AppCompatActivity? ?: return
        if (!permissionHelper.checkCallPermission(activity)) {
            pendingPhone = clearedPhone
            permissionHelper.requestCallPermission(this)
            return
        }
        startActivity(intent)
    }

    private fun closeScreenValidated() {
        view?.let { Navigation.findNavController(it).navigateUp() }
    }
    //</editor-fold>


    interface Listener {
        fun provideAvatarUrl(): String?
    }


    companion object {
        private const val LOG_TAG = "HeroDetailsFragment"
    }
}