package com.devtau.ironHeroes.ui.activities.main

import android.content.Intent
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.devtau.ironHeroes.IronHeroesApp
import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.adapters.FunctionsPagerAdapter
import com.devtau.ironHeroes.ui.Coordinator
import com.devtau.ironHeroes.ui.DependencyRegistry
import com.devtau.ironHeroes.ui.activities.ViewSubscriberActivity
import com.devtau.ironHeroes.ui.fragments.getCurrentNavigationFragment
import com.devtau.ironHeroes.ui.fragments.heroDetails.HeroDetailsFragment
import com.devtau.ironHeroes.ui.fragments.settings.SettingsFragment
import com.devtau.ironHeroes.ui.fragments.trainingsList.TrainingsFragment
import com.vk.sdk.VKSdk
import com.vk.sdk.api.VKApiConst
import com.vk.sdk.api.VKParameters
import com.vk.sdk.api.VKRequest
import com.vk.sdk.api.VKResponse
import io.reactivex.functions.Action

class MainActivity: ViewSubscriberActivity(),
    MainContract.View,
    SettingsFragment.Listener,
    HeroDetailsFragment.Listener {

    private lateinit var presenter: MainContract.Presenter
    private lateinit var coordinator: Coordinator
    private lateinit var navController : NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    private var pageIndex: Int = 0
    private var pagerAdapter: FunctionsPagerAdapter? = null
    private var heroAvatarUrl: String? = null


    //<editor-fold desc="Framework overrides">
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        DependencyRegistry.inject(this)
        if (savedInstanceState != null) pageIndex = savedInstanceState.getInt(PAGE_INDEX)
        initUi()
        navController = findNavController(R.id.mainContainer)
        NavigationUI.setupActionBarWithNavController(this, navController)
//        findViewById<BottomNavigationView>(R.id.bottom_nav).setupWithNavController(navController)
//        appBarConfiguration = AppBarConfiguration(navController.graph)
    }

    override fun onStart() {
        super.onStart()
        presenter.restartLoaders()
    }

    override fun onStop() {
        super.onStop()
        presenter.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(PAGE_INDEX, pageIndex)
        super.onSaveInstanceState(outState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        val authListener = IronHeroesApp.getVKAuthListener(this)
        if (VKSdk.onActivityResult(requestCode, resultCode, intent, authListener)) {
            val params = VKParameters()
            params[VKApiConst.FIELDS] = "photo_max_orig"

            val request = VKRequest("users.get", params)
            request.executeWithListener(object: VKRequest.VKRequestListener() {
                override fun onComplete(response: VKResponse?) {
                    super.onComplete(response)
                    val resp = response?.json?.getJSONArray("response")
                    val user = resp?.getJSONObject(0)
                    heroAvatarUrl = user?.getString("photo_max_orig")
                    with(supportFragmentManager.getCurrentNavigationFragment()) {
                        if (this is HeroDetailsFragment) updateHeroData("avatar", heroAvatarUrl)
                    }
                }
            })
        } else super.onActivityResult(requestCode, resultCode, intent)
    }

    override fun onSupportNavigateUp() = with (supportFragmentManager.getCurrentNavigationFragment()) {
        if (this is HeroDetailsFragment) {
            onBackPressed(Action { navController.navigateUp() })
//            onBackPressed(Action { navController.navigateUp(appBarConfiguration) })
            true
        } else {
            navController.navigateUp()
        }
    }
    //</editor-fold>


    //<editor-fold desc="Interface overrides">
    override fun getLogTag() = LOG_TAG

    override fun updateSpinnersVisibility() = (pagerAdapter?.getItem(0) as TrainingsFragment).updateSpinnersVisibility()

    override fun provideAvatarUrl(): String? = heroAvatarUrl
    //</editor-fold>


    fun configureWith(presenter: MainContract.Presenter, coordinator: Coordinator) {
        this.presenter = presenter
        this.coordinator = coordinator
    }


    //<editor-fold desc="Private methods">
    private fun initUi() {

    }
    //</editor-fold>


    companion object {
        private const val LOG_TAG = "FunctionsActivity"
        private const val PAGE_INDEX = "pageIndex"
    }
}