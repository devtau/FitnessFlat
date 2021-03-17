package com.devtau.ironHeroes.util

import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.Transformation
import android.widget.ProgressBar

object Animator {

    fun animateProgressBar(view: ProgressBar?, from: Float, to: Float, duration: Long) {
        view?.startAnimation(ProgressBarAnimation(view, from, to, duration))
    }


    private class ProgressBarAnimation(
        private val progressBar: ProgressBar?,
        private val from: Float,
        private val to: Float,
        mDuration: Long
    ) : Animation() {
        init {
            duration = mDuration
            interpolator = LinearInterpolator()
        }

        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            super.applyTransformation(interpolatedTime, t)
            val value = from + (to - from) * interpolatedTime
            progressBar?.progress = value.toInt()
        }
    }
}
