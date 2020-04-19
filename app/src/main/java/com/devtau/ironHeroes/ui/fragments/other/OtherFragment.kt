package com.devtau.ironHeroes.ui.fragments.other

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.data.model.wrappers.ImpExData
import com.devtau.ironHeroes.databinding.FragmentOtherBinding
import com.devtau.ironHeroes.ui.fragments.BaseFragment
import com.devtau.ironHeroes.ui.fragments.getViewModelFactory
import com.devtau.ironHeroes.util.EventObserver
import com.devtau.ironHeroes.util.PermissionHelperImpl
import com.devtau.ironHeroes.util.setupSnackbar
import com.devtau.ironHeroes.util.showDialog
import io.reactivex.functions.Action

class OtherFragment: BaseFragment() {

    private val _viewModel by viewModels<OtherViewModel> { getViewModelFactory() }
    private lateinit var exchangeDirName: String
    private var exportRequested: Boolean = true


    //<editor-fold desc="Framework overrides">
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentOtherBinding.inflate(inflater, container, false).apply {
            viewModel = _viewModel
            lifecycleOwner = viewLifecycleOwner
            _viewModel.subscribeToVM()
        }
        exchangeDirName = getString(R.string.app_name)
        return binding.root
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
        view?.setupSnackbar(viewLifecycleOwner, snackbarText)

        trainings.observe(viewLifecycleOwner, Observer {/*NOP*/})

        exercises.observe(viewLifecycleOwner, Observer {/*NOP*/})

        openHero.observe(viewLifecycleOwner, EventObserver {
            coordinator.launchHeroes(view, it)
        })

        openDBViewer.observe(viewLifecycleOwner, EventObserver {
            coordinator.launchDBViewer(context)
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
            view?.showDialog(LOG_TAG, R.string.clear_db_confirm, Action {
                clearDBConfirmed()
            })
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


    companion object {
        private const val LOG_TAG = "OtherFragment"
    }
}