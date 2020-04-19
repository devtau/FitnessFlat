package com.devtau.ironHeroes.ui.fragments.functions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.adapters.FunctionsPagerAdapter
import com.devtau.ironHeroes.databinding.FragmentFunctionsBinding
import com.devtau.ironHeroes.ui.fragments.BaseFragment
import com.devtau.ironHeroes.ui.fragments.getViewModelFactory
import com.devtau.ironHeroes.util.EventObserver
import com.devtau.ironHeroes.util.Logger
import com.devtau.ironHeroes.util.setupSnackbar
import com.devtau.ironHeroes.util.showDialog
import io.reactivex.functions.Action

class FunctionsFragment: BaseFragment() {

    private val _viewModel by viewModels<FunctionsViewModel> { getViewModelFactory() }
    private var pageIndex: Int = 0


    //<editor-fold desc="Framework overrides">
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentFunctionsBinding.inflate(inflater, container, false).apply {
            viewModel = _viewModel
            lifecycleOwner = viewLifecycleOwner
            _viewModel.subscribeToVM(this)

            initUi()
            if (savedInstanceState != null) pageIndex = savedInstanceState.getInt(PAGE_INDEX)
            turnPage(pageIndex, this)
        }
        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(PAGE_INDEX, pageIndex)
        super.onSaveInstanceState(outState)
    }
    //</editor-fold>


    //<editor-fold desc="Private methods">
    private fun FunctionsViewModel.subscribeToVM(binding: FragmentFunctionsBinding) {
        view?.setupSnackbar(viewLifecycleOwner, _viewModel.snackbarText)

        turnPage.observe(viewLifecycleOwner, EventObserver {
            turnPage(it, binding)
        })

        showDemoConfigDialog.observe(viewLifecycleOwner, EventObserver {
            view?.showDialog(LOG_TAG, R.string.load_demo_configuration, Action {
                context?.let { _viewModel.loadDemoConfigConfirmed(it) }
            }, Action {
                context?.let { _viewModel.loadDemoConfigDeclined(it) }
            })
        })

        showCreateHeroesDialog.observe(viewLifecycleOwner, EventObserver {
            view?.showDialog(LOG_TAG, R.string.create_heroes, Action {
                turnPage(3, binding)
                _viewModel.createHeroesConfirmed()
            }, Action {
                _viewModel.createHeroesDeclined()
            })
        })
    }

    private fun FragmentFunctionsBinding.initUi() {
        functionsPager.adapter = FunctionsPagerAdapter(this@FunctionsFragment)
        functionsPager.offscreenPageLimit = 3
        functionsPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) = turnPage(position, this@initUi)
        })
    }

    private fun turnPage(pageIndex: Int, binding: FragmentFunctionsBinding) {
        Logger.d(LOG_TAG, "turnPage. pageIndex=$pageIndex")
        this.pageIndex = pageIndex
        applyPageIndicatorState(pageIndex, binding.trainings, binding.statistics, binding.settings, binding.other)
        binding.functionsPager.currentItem = pageIndex
    }

    private fun applyPageIndicatorState(
        pageIndex: Int, trainings: TextView?, statistics: TextView?, settings: TextView?, other: TextView?
    ) = context?.let {
        val colorActive = ContextCompat.getColor(it, R.color.colorAccent)
        val colorInactive = ContextCompat.getColor(it, R.color.secondaryTextColor)

        val page0IconActive = ContextCompat.getDrawable(it, R.drawable.ic_workouts_yellow)
        val page1IconActive = ContextCompat.getDrawable(it, R.drawable.ic_statistics_yellow)
        val page2IconActive = ContextCompat.getDrawable(it, R.drawable.ic_settings_yellow)
        val page3IconActive = ContextCompat.getDrawable(it, R.drawable.ic_other_yellow)

        val page0IconInactive = ContextCompat.getDrawable(it, R.drawable.ic_workouts_gray)
        val page1IconInactive = ContextCompat.getDrawable(it, R.drawable.ic_statistics_gray)
        val page2IconInactive = ContextCompat.getDrawable(it, R.drawable.ic_settings_gray)
        val page3IconInactive = ContextCompat.getDrawable(it, R.drawable.ic_other_gray)

        trainings?.setCompoundDrawablesWithIntrinsicBounds(null, if (pageIndex == 0) page0IconActive else page0IconInactive, null, null)
        statistics?.setCompoundDrawablesWithIntrinsicBounds(null, if (pageIndex == 1) page1IconActive else page1IconInactive, null, null)
        settings?.setCompoundDrawablesWithIntrinsicBounds(null, if (pageIndex == 2) page2IconActive else page2IconInactive, null, null)
        other?.setCompoundDrawablesWithIntrinsicBounds(null, if (pageIndex == 3) page3IconActive else page3IconInactive, null, null)

        trainings?.setTextColor(if (pageIndex == 0) colorActive else colorInactive)
        statistics?.setTextColor(if (pageIndex == 1) colorActive else colorInactive)
        settings?.setTextColor(if (pageIndex == 2) colorActive else colorInactive)
        other?.setTextColor(if (pageIndex == 3) colorActive else colorInactive)
    }
    //</editor-fold>


    companion object {
        private const val LOG_TAG = "FunctionsFragment"
        private const val PAGE_INDEX = "pageIndex"
    }
}