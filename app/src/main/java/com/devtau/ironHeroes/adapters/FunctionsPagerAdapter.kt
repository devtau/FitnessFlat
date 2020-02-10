package com.devtau.ironHeroes.adapters

import android.os.Parcelable
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.devtau.ironHeroes.ui.Coordinator
import com.devtau.ironHeroes.ui.fragments.other.OtherFragment
import com.devtau.ironHeroes.ui.fragments.settings.SettingsFragment
import com.devtau.ironHeroes.ui.fragments.statistics.StatisticsFragment
import com.devtau.ironHeroes.ui.fragments.trainingsList.TrainingsFragment
import com.devtau.ironHeroes.util.Logger
import java.lang.ref.WeakReference
import java.util.*

class FunctionsPagerAdapter(fragmentManager: FragmentManager, val coordinator: Coordinator):
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
            0 -> TrainingsFragment()
            1 -> StatisticsFragment()
            2 -> SettingsFragment()
            3 -> OtherFragment()
            else -> OtherFragment()
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