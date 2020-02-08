package com.devtau.ironHeroes.ui.fragments.other

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.devtau.ironHeroes.Coordinator
import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.data.model.Hero
import com.devtau.ironHeroes.enums.HumanType
import com.devtau.ironHeroes.ui.DependencyRegistry
import com.devtau.ironHeroes.ui.fragments.ViewSubscriberFragment
import com.devtau.ironHeroes.util.PermissionHelperImpl
import io.reactivex.functions.Action
import kotlinx.android.synthetic.main.fragment_other.*

class OtherFragment: ViewSubscriberFragment(), OtherContract.View {

    private lateinit var presenter: OtherContract.Presenter
    private lateinit var coordinator: Coordinator
    private var exportRequested: Boolean = true


    //<editor-fold desc="Framework overrides">
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DependencyRegistry.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_other, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    override fun onStart() {
        super.onStart()
        presenter.restartLoaders()
    }

    override fun onStop() {
        super.onStop()
        presenter.onStop()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PermissionHelperImpl.STORAGE_REQUEST_CODE &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (exportRequested) presenter.exportToFile() else presenter.importFromFile()
        }
    }
    //</editor-fold>


    //<editor-fold desc="Interface overrides">
    override fun getLogTag() = LOG_TAG

    override fun showExported(trainingsCount: Int, exercisesCount: Int) {
        val trainings = resources.getQuantityString(R.plurals.trainings, trainingsCount, trainingsCount)
        val exercises = resources.getQuantityString(R.plurals.exercises, exercisesCount, exercisesCount)
        showMsg(String.format(getString(R.string.exported_formatter), trainings, exercises))
    }

    override fun showReadFromFile(trainingsCount: Int, exercisesCount: Int) {
        val trainings = resources.getQuantityString(R.plurals.trainings, trainingsCount, trainingsCount)
        val exercises = resources.getQuantityString(R.plurals.exercises, exercisesCount, exercisesCount)
        showMsg(String.format(getString(R.string.imported_formatter), trainings, exercises))
    }

    override fun provideMockHeroes(): List<Hero> {
        val context = context
        return if (context == null) ArrayList() else Hero.getMockHeroes(context)
    }
    //</editor-fold>


    fun configureWith(presenter: OtherContract.Presenter, coordinator: Coordinator) {
        this.presenter = presenter
        this.coordinator = coordinator
    }


    //<editor-fold desc="Private methods">
    private fun initUi() {
        heroes?.setOnClickListener {
            coordinator.launchHeroesActivity(context, HumanType.HERO)
        }
        champions?.setOnClickListener {
            coordinator.launchHeroesActivity(context, HumanType.CHAMPION)
        }
        exportToFile?.setOnClickListener {
            val context = context ?: return@setOnClickListener
            val permissionHelper = PermissionHelperImpl()
            if (!permissionHelper.checkStoragePermission(context)) {
                permissionHelper.requestStoragePermission(this)
                exportRequested = true
                return@setOnClickListener
            }
            presenter.exportToFile()
        }
        importFromFile?.setOnClickListener {
            val context = context ?: return@setOnClickListener
            val permissionHelper = PermissionHelperImpl()
            if (!permissionHelper.checkStoragePermission(context)) {
                permissionHelper.requestStoragePermission(this)
                exportRequested = false
                return@setOnClickListener
            }
            presenter.importFromFile()
        }
        clearDB?.setOnClickListener {
            showMsg(R.string.clear_db_confirm, Action { presenter.clearDB() }, Action {  })
        }
    }
    //</editor-fold>


    companion object {
        private const val LOG_TAG = "OtherFragment"
    }
}