package com.devtau.ironHeroes.adapters

import android.os.Parcelable
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.devtau.ironHeroes.data.model.Hero
import com.devtau.ironHeroes.ui.fragments.other.OtherFragment
import com.devtau.ironHeroes.ui.fragments.settings.SettingsFragment
import com.devtau.ironHeroes.ui.fragments.statistics.StatisticsFragment
import com.devtau.ironHeroes.ui.fragments.trainingsList.TrainingsFragment
import com.devtau.ironHeroes.util.Logger
import java.lang.ref.WeakReference
import java.util.*

class FunctionsPagerAdapter(fragmentManager: FragmentManager):
    FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val pages: HashMap<Int, WeakReference<Fragment>> = HashMap()

    override fun instantiateItem(container: ViewGroup, position: Int): Fragment {
        Logger.d(LOG_TAG, "instantiateItem. position=$position")
        val fragment = super.instantiateItem(container, position) as Fragment
        var page: WeakReference<Fragment>? = pages[position]
        if (page == null) {
            page = WeakReference(fragment)
            pages[position] = page
        }
        return fragment
    }

    override fun getCount(): Int = 4

    override fun getItem(position: Int): Fragment {
        Logger.d(LOG_TAG, "getItem. position=$position")
        return pages[position]?.get() ?: when (position) {
            0 -> TrainingsFragment.newInstance()
            1 -> StatisticsFragment.newInstance(Hero.getMockHeroes()[0].id!!)
            2 -> SettingsFragment.newInstance()
            3 -> OtherFragment.newInstance()
            else -> OtherFragment.newInstance()
        }
    }

    override fun saveState(): Parcelable? {
        Logger.d(LOG_TAG, "saveState")
        return super.saveState()
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {
        Logger.d(LOG_TAG, "restoreState")
        super.restoreState(state, loader)
    }


    companion object {
        private const val LOG_TAG = "FunctionsPagerAdapter"
    }
}