package com.devtau.ironHeroes.ui.activities.launcher

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.data.model.Hero
import com.devtau.ironHeroes.enums.HumanType
import com.devtau.ironHeroes.ui.DependencyRegistry
import com.devtau.ironHeroes.ui.activities.DBViewerActivity
import com.devtau.ironHeroes.ui.activities.ViewSubscriberActivity
import com.devtau.ironHeroes.ui.activities.heroesList.HeroesActivity
import com.devtau.ironHeroes.ui.activities.statistics.StatisticsActivity
import com.devtau.ironHeroes.ui.activities.trainingsList.TrainingsActivity
import com.devtau.ironHeroes.util.AppUtils
import com.devtau.ironHeroes.util.Logger
import com.devtau.ironHeroes.util.PermissionHelperImpl
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_launcher.*

class LauncherActivity: ViewSubscriberActivity(), LauncherView {

    lateinit var presenter: LauncherPresenter
    var exportRequested: Boolean = true


    //<editor-fold desc="Framework overrides">
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)
        DependencyRegistry().inject(this)
        AppUtils.initToolbar(this, R.string.choose_action, false)
        initUi()
        sendTestToFireStore()
    }

    override fun onStart() {
        super.onStart()
        presenter.restartLoaders()
    }

    override fun onStop() {
        super.onStop()
        presenter.onStop()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.menu_launcher, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.openDB -> {
            DBViewerActivity.newInstance(this)
            true
        }
        else -> super.onOptionsItemSelected(item)
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
    private fun initUi() {
        heroes.setOnClickListener { HeroesActivity.newInstance(this, HumanType.HERO) }
        champions.setOnClickListener { HeroesActivity.newInstance(this, HumanType.CHAMPION) }
        trainings.setOnClickListener { TrainingsActivity.newInstance(this) }
        statistics.setOnClickListener { StatisticsActivity.newInstance(this, Hero.getMockHeroes()[0].id!!) }
        database.setOnClickListener { DBViewerActivity.newInstance(this) }
        exportToFile.setOnClickListener {
            val permissionHelper = PermissionHelperImpl()
            if (!permissionHelper.checkStoragePermission(this)) {
                permissionHelper.requestStoragePermission(this)
                exportRequested = true
                return@setOnClickListener
            }
            presenter.exportToFile()
        }
        importFromFile.setOnClickListener {
            val permissionHelper = PermissionHelperImpl()
            if (!permissionHelper.checkStoragePermission(this)) {
                permissionHelper.requestStoragePermission(this)
                exportRequested = false
                return@setOnClickListener
            }
            presenter.importFromFile()
        }
    }

    private fun sendTestToFireStore() {
        val db = FirebaseFirestore.getInstance()
        val exerciseInTraining = hashMapOf(
            "id" to 1,
            "trainingId" to 1,
            "exerciseId" to 41,
            "weight" to 0,
            "repeats" to 3,
            "count" to 20,
            "comment" to ""
        )

        db.collection("ExerciseInTraining")
            .add(exerciseInTraining)
            .addOnSuccessListener { documentReference ->
                Logger.d(LOG_TAG, "document added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Logger.w(LOG_TAG, "Error adding document $e")
            }
    }
    //</editor-fold>


    companion object {
        private const val LOG_TAG = "LauncherActivity"
    }
}