package com.devtau.ironHeroes.ui.activities.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.devtau.ironHeroes.IronHeroesApp
import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.ui.fragments.getCurrentNavigationFragment
import com.devtau.ironHeroes.ui.fragments.heroDetails.HeroDetailsFragment
import com.vk.sdk.VKSdk
import com.vk.sdk.api.VKApiConst
import com.vk.sdk.api.VKParameters
import com.vk.sdk.api.VKRequest
import com.vk.sdk.api.VKResponse
import io.reactivex.functions.Action

class MainActivity: AppCompatActivity(), HeroDetailsFragment.Listener {

    private lateinit var navController : NavController
    private var heroAvatarUrl: String? = null


    //<editor-fold desc="Framework overrides">
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navController = findNavController(R.id.mainContainer)
        NavigationUI.setupActionBarWithNavController(this, navController)
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
            true
        } else {
            navController.navigateUp()
        }
    }
    //</editor-fold>


    override fun provideAvatarUrl(): String? = heroAvatarUrl


    companion object {
        private const val LOG_TAG = "MainActivity"
    }
}