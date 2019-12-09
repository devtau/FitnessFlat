package com.devtau.ironHeroes.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.devtau.ironHeroes.R

class ActionsFragment: Fragment() {

    private var listener: Listener? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Listener) listener = context
        else throw RuntimeException("$context must implement $LOG_TAG Listener")
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_launcher, container, false)
    }


    interface Listener {

    }


    companion object {
        const val FRAGMENT_TAG = "com.devtau.ironHeroes.ui.fragments.ActionsFragment"
        private const val LOG_TAG = "ActionsFragment"

        fun newInstance(): ActionsFragment {
            val fragment = ActionsFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}
