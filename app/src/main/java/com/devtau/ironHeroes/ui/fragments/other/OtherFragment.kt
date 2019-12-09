package com.devtau.ironHeroes.ui.fragments.other

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.enums.HumanType
import com.devtau.ironHeroes.ui.DependencyRegistry
import com.devtau.ironHeroes.ui.activities.DBViewerActivity
import com.devtau.ironHeroes.ui.activities.heroesList.HeroesActivity
import com.devtau.ironHeroes.ui.fragments.ViewSubscriberFragment
import com.devtau.ironHeroes.util.PermissionHelperImpl

class OtherFragment: ViewSubscriberFragment(), OtherView {

    lateinit var presenter: OtherPresenter
    private var exportRequested: Boolean = true


    //<editor-fold desc="Framework overrides">
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DependencyRegistry().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_other, container, false)
        initUi(root)
        return root
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


    //<editor-fold desc="View overrides">
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
    //</editor-fold>


    //<editor-fold desc="Private methods">
    private fun initUi(root: View) {
        root.findViewById<View>(R.id.heroes)?.setOnClickListener { HeroesActivity.newInstance(context, HumanType.HERO) }
        root.findViewById<View>(R.id.champions)?.setOnClickListener { HeroesActivity.newInstance(context, HumanType.CHAMPION) }
        root.findViewById<View>(R.id.database)?.setOnClickListener { DBViewerActivity.newInstance(context) }
        root.findViewById<View>(R.id.exportToFile)?.setOnClickListener {
            val context = context ?: return@setOnClickListener
            val permissionHelper = PermissionHelperImpl()
            if (!permissionHelper.checkStoragePermission(context)) {
                permissionHelper.requestStoragePermission(this)
                exportRequested = true
                return@setOnClickListener
            }
            presenter.exportToFile()
        }
        root.findViewById<View>(R.id.importFromFile)?.setOnClickListener {
            val context = context ?: return@setOnClickListener
            val permissionHelper = PermissionHelperImpl()
            if (!permissionHelper.checkStoragePermission(context)) {
                permissionHelper.requestStoragePermission(this)
                exportRequested = false
                return@setOnClickListener
            }
            presenter.importFromFile()
        }
    }
    //</editor-fold>


    companion object {
        const val FRAGMENT_TAG = "com.devtau.ironHeroes.ui.fragments.other.OtherFragment"
        private const val LOG_TAG = "OtherFragment"

        fun newInstance(): OtherFragment {
            val fragment = OtherFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}