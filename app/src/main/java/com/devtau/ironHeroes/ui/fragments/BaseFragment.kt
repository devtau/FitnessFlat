package com.devtau.ironHeroes.ui.fragments

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.devtau.ironHeroes.IronHeroesApp
import com.devtau.ironHeroes.ViewModelFactory
import com.devtau.ironHeroes.ui.Coordinator
import com.devtau.ironHeroes.ui.CoordinatorImpl
import com.devtau.ironHeroes.ui.ResourceResolver
import com.devtau.ironHeroes.util.Event
import com.devtau.ironHeroes.util.showSnackbar

abstract class BaseFragment: Fragment(),
    Coordinator by CoordinatorImpl {

    private val resourceResolver = object: ResourceResolver {
        override fun resolveColor(@ColorRes colorResId: Int): Int = context?.getColor(colorResId) ?: 0
        override fun resolveString(@StringRes stringResId: Int): String = context?.getString(stringResId) ?: ""
    }

    fun getViewModelFactory() = getViewModelFactory(this, resourceResolver)

    fun tryToShowSnackbar(msgEvent: Event<Int>) {
        msgEvent.getContentIfNotHandled()?.let {
            view?.showSnackbar(it)
        }
    }

    companion object {
        fun getViewModelFactory(
            fragment: Fragment,
            resourceResolver: ResourceResolver? = null
        ) = with(fragment.requireContext().applicationContext as IronHeroesApp) {
            ViewModelFactory(
                trainingsRepository,
                heroesRepository,
                exercisesInTrainingsRepository,
                exercisesRepository,
                muscleGroupsRepository,
                fragment, fragment.arguments, resourceResolver
            )
        }
    }
}