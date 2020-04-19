package com.devtau.ironHeroes.ui.fragments

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.devtau.ironHeroes.IronHeroesApp
import com.devtau.ironHeroes.ViewModelFactory
import com.devtau.ironHeroes.ui.Coordinator
import com.devtau.ironHeroes.ui.CoordinatorImpl
import com.devtau.ironHeroes.ui.ResourceResolver
import com.devtau.ironHeroes.util.Logger

abstract class BaseFragment: Fragment() {

    val coordinator: Coordinator = CoordinatorImpl


    fun logLifeCycle(logTag: String) {
        viewLifecycleOwner.lifecycle.addObserver(object: LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_CREATE) fun created() = Logger.d(logTag, "ON_CREATE")
            @OnLifecycleEvent(Lifecycle.Event.ON_START) fun started() = Logger.d(logTag, "ON_START")
            @OnLifecycleEvent(Lifecycle.Event.ON_RESUME) fun resumed() = Logger.d(logTag, "ON_RESUME")
            @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE) fun paused() = Logger.d(logTag, "ON_PAUSE")
            @OnLifecycleEvent(Lifecycle.Event.ON_STOP) fun stopped() = Logger.d(logTag, "ON_STOP")
        })
    }
}

fun FragmentManager.getCurrentNavigationFragment(): Fragment? =
    primaryNavigationFragment?.childFragmentManager?.fragments?.first()

fun FragmentActivity?.initActionBar(titleId: Int?, show: Boolean = true): Boolean {
    val actionbar = (this as AppCompatActivity?)?.supportActionBar
    if (this == null || actionbar == null) return false
    if (show) actionbar.show() else actionbar.hide()
    actionbar.title = if (titleId == null) "" else this.getString(titleId)
    return true
}

fun Fragment.getViewModelFactory(
    resourceResolver: ResourceResolver? = null
): ViewModelFactory {
    val app = requireContext().applicationContext as IronHeroesApp
    return ViewModelFactory(
        app.trainingsRepository,
        app.heroesRepository,
        app.exercisesInTrainingsRepository,
        app.exercisesRepository,
        app.muscleGroupsRepository,
        this, arguments, resourceResolver)
}