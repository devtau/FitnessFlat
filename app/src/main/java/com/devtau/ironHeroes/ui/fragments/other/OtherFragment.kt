package com.devtau.ironHeroes.ui.fragments.other

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.data.model.wrappers.ImpExData
import com.devtau.ironHeroes.databinding.FragmentOtherBinding
import com.devtau.ironHeroes.ui.fragments.BaseFragment
import com.devtau.ironHeroes.util.*

class OtherFragment: BaseFragment() {

    private val _viewModel by viewModels<OtherViewModel> { getViewModelFactory() }
    private lateinit var exchangeDirName: String
    private var exportRequested: Boolean = true
    private var listener: Listener? = null


    //<editor-fold desc="Framework overrides">
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentOtherBinding.inflate(inflater, container, false).apply {
            viewModel = _viewModel
            lifecycleOwner = viewLifecycleOwner
            _viewModel.subscribeToVM()
        }
        exchangeDirName = getString(R.string.app_name)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = when {
            context is Listener -> context
            parentFragment is Listener -> parentFragment as Listener
            else -> throw RuntimeException("$context must implement $LOG_TAG Listener")
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PermissionHelperImpl.STORAGE_REQUEST_CODE &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (exportRequested) _viewModel.exportToFileConfirmed(exchangeDirName)
            else _viewModel.importFromFileConfirmed(exchangeDirName)
        }
    }
    //</editor-fold>


    //<editor-fold desc="Private methods">
    private fun OtherViewModel.subscribeToVM() {
        snackbarText.observe(viewLifecycleOwner, ::tryToShowSnackbar)

        trainings.observe(viewLifecycleOwner, {/*NOP*/})

        exercises.observe(viewLifecycleOwner, {/*NOP*/})

        openHero.observe(viewLifecycleOwner, EventObserver {
            launchHeroes(view, it)
        })

        exportedToFile.observe(viewLifecycleOwner, EventObserver {
            showExported(it)
        })

        importedFromFile.observe(viewLifecycleOwner, EventObserver {
            showImported(it)
        })

        exportToFile.observe(viewLifecycleOwner, EventObserver {
            val context = context ?: return@EventObserver
            val permissionHelper = PermissionHelperImpl()
            if (!permissionHelper.checkStoragePermission(context)) {
                permissionHelper.requestStoragePermission(this@OtherFragment)
                exportRequested = true
                return@EventObserver
            }
            exportToFileConfirmed(exchangeDirName)
        })

        importFromFile.observe(viewLifecycleOwner, EventObserver {
            val context = context ?: return@EventObserver
            val permissionHelper = PermissionHelperImpl()
            if (!permissionHelper.checkStoragePermission(context)) {
                permissionHelper.requestStoragePermission(this@OtherFragment)
                exportRequested = false
                return@EventObserver
            }
            importFromFileConfirmed(exchangeDirName)
        })

        clearDB.observe(viewLifecycleOwner, EventObserver {
            view?.showDialog(LOG_TAG, R.string.clear_db_confirm, {
                clearDBConfirmed()
            })
        })

        loadDemoConfig.observe(viewLifecycleOwner, EventObserver {
            listener?.loadDemoConfig()
        })
    }

    private fun showExported(wrapper: ImpExData) {
        val trainings = resources.getQuantityString(R.plurals.trainings, wrapper.trainingsCount, wrapper.trainingsCount)
        val exercises = resources.getQuantityString(R.plurals.exercises, wrapper.exercisesCount, wrapper.exercisesCount)
        view?.showDialog(LOG_TAG, String.format(getString(R.string.exported_formatter), trainings, exercises))
    }

    private fun showImported(wrapper: ImpExData) {
        val trainings = resources.getQuantityString(R.plurals.trainings, wrapper.trainingsCount, wrapper.trainingsCount)
        val exercises = resources.getQuantityString(R.plurals.exercises, wrapper.exercisesCount, wrapper.exercisesCount)
        view?.showDialog(LOG_TAG, String.format(getString(R.string.imported_formatter), trainings, exercises))
    }
    //</editor-fold>

    interface Listener {
        fun loadDemoConfig()
    }


    companion object {
        private const val LOG_TAG = "OtherFragment"
    }
}