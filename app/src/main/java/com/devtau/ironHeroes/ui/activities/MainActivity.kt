package com.devtau.ironHeroes.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.devtau.ironHeroes.IronHeroesApp
import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.ui.fragments.heroDetails.HeroDetailsFragment
import com.vk.sdk.VKSdk
import com.vk.sdk.api.VKApiConst
import com.vk.sdk.api.VKParameters
import com.vk.sdk.api.VKRequest
import com.vk.sdk.api.VKResponse

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
                }
            })
        } else super.onActivityResult(requestCode, resultCode, intent)
    }

    override fun onSupportNavigateUp(): Boolean {
        val top = supportFragmentManager.primaryNavigationFragment?.childFragmentManager?.fragments?.first()
        return if (top is HeroDetailsFragment) {
            top.tryCloseScreen()
            true
        } else {
            if (!navController.navigateUp()) super.onBackPressed()
            true
        }
    }

    override fun onBackPressed() {
        onSupportNavigateUp()
    }
    //</editor-fold>


    override fun provideAvatarUrl(): String? = heroAvatarUrl
}