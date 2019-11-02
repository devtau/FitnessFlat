package com.devtau.ironHeroes.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator

object Animator {

    const val CLOSED_DROPDOWN_HEIGHT = 0
    const val ANIMATION_LENGTH_LONG = 700L
    const val ANIMATION_LENGTH_MED = 500L
    const val ANIMATION_LENGTH_SHORT = 300L

    private const val LOG_TAG = "AnimatorHelper"
    private var animatorSet: AnimatorSet? = null


    fun animateShowUpCircular(animatedView: View) {
        if (Build.VERSION.SDK_INT >= 21) {
            showViewCircular(animatedView)
        } else {
            showView(animatedView)
        }
    }

    fun animateHideCircular(animatedView: View) {
        if (Build.VERSION.SDK_INT >= 21) {
            hideViewCircular(animatedView)
        } else {
            hideView(animatedView)
        }
    }


    @TargetApi(21)
    private fun showViewCircular(animatedView: View?) {
        animatedView ?: return
        animatedView.alpha = 1f
        animatedView.visibility = View.VISIBLE
        val cx = animatedView.width / 2
        val cy = animatedView.height / 2
        val finalRadius = calculateRadius(animatedView)
        val anim = ViewAnimationUtils.createCircularReveal(animatedView, cx, cy, 0f, finalRadius)
        anim.duration = ANIMATION_LENGTH_LONG
        anim.start()
    }

    private fun showView(animatedView: View?) {
        animatedView ?: return
        animatedView.alpha = 1f
        animatedView.visibility = View.VISIBLE
        val fadeIn = ObjectAnimator.ofFloat(animatedView, "alpha", 0f, 1f)
        fadeIn.duration = ANIMATION_LENGTH_LONG
        fadeIn.start()
    }

    @TargetApi(21)
    private fun hideViewCircular(animatedView: View?) {
        animatedView ?: return
        val cx = animatedView.width / 2
        val cy = animatedView.height / 2
        val finalRadius = calculateRadius(animatedView)
        val anim = ViewAnimationUtils.createCircularReveal(animatedView, cx, cy, finalRadius, 0f)
        anim.duration = ANIMATION_LENGTH_LONG
        anim.addListener(object: Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                animatedView.visibility = View.INVISIBLE
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
        anim.start()
    }

    private fun hideView(animatedView: View?) {
        if (animatedView == null) return
        animatedView.alpha = 1f
        animatedView.visibility = View.VISIBLE
        val fadeIn = ObjectAnimator.ofFloat(animatedView, "alpha", 1f, 0f)
        fadeIn.duration = ANIMATION_LENGTH_LONG
        fadeIn.start()
    }

    private fun calculateRadius(animatedView: View): Float =
        Math.sqrt((animatedView.width * animatedView.width + animatedView.height * animatedView.height).toDouble()).toFloat() / 2

    fun toggleViewImage(imageView: ImageView?, context: Context, targetResourceId: Int) {
        if (imageView == null) return
        val imageDrawable = ContextCompat.getDrawable(context, targetResourceId)
        imageView.animate().alpha(0f).setDuration(ANIMATION_LENGTH_SHORT).withEndAction {
            imageView.setImageDrawable(imageDrawable)
            imageView.animate().alpha(1f).duration = ANIMATION_LENGTH_SHORT
        }.start()
    }

    fun toggleViews(viewToHide: View?, viewToShow: View?) {
        if (viewToHide == null || viewToShow == null || viewToHide.visibility == View.INVISIBLE || viewToShow.visibility == View.VISIBLE) return
        viewToHide.animate().alpha(0f).setDuration(ANIMATION_LENGTH_SHORT).withEndAction { viewToHide.visibility = View.INVISIBLE }.start()
        viewToShow.alpha = 0f
        viewToShow.visibility = View.VISIBLE
        viewToShow.animate().alpha(1f).setDuration(ANIMATION_LENGTH_SHORT).start()
    }

    @SuppressLint("NewApi")
    fun animateHorizontalSlide(removeFromCartContainer: View, removeFromCartBackground: View, widthToMove: Int, left: Boolean) {
        val moveDropdownMenu: ObjectAnimator = if (left) {
            ObjectAnimator.ofFloat(removeFromCartContainer, "translationX", widthToMove.toFloat(), 0f)
        } else {
            ObjectAnimator.ofFloat(removeFromCartContainer, "translationX", 0f, widthToMove.toFloat())
        }

        animatorSet = AnimatorSet()
        animatorSet?.interpolator = if (left) AccelerateInterpolator() else DecelerateInterpolator()
        animatorSet?.play(moveDropdownMenu)
        animatorSet?.duration = ANIMATION_LENGTH_SHORT
        animatorSet?.addListener(createCartListener(removeFromCartContainer, removeFromCartBackground, left))
        animatorSet?.start()
    }

    private fun createCartListener(animatedView: View, removeFromCartBackground: View, show: Boolean): AnimatorListenerAdapter {
        val listener: AnimatorListenerAdapter
        if (show) {
            listener = object: AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator) {
                    removeFromCartBackground.visibility = View.VISIBLE
                    animatedView.visibility = View.VISIBLE
                }

                override fun onAnimationEnd(animator: Animator) {
                    val animatorLoc = ObjectAnimator.ofFloat(animatedView, "translationX", 0.0f, 0.0f)
                    animatorLoc.duration = 1
                    animatorLoc.start()
                    removeFromCartBackground.visibility = View.GONE
                }
            }
        } else {
            listener = object: AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator) {
                    removeFromCartBackground.visibility = View.VISIBLE
                }

                override fun onAnimationEnd(animator: Animator) {
                    val animatorLoc = ObjectAnimator.ofFloat(animatedView, "translationX", 0.0f, 0.0f)
                    animatorLoc.duration = 1
                    animatorLoc.start()
                    animatedView.visibility = View.GONE
                    removeFromCartBackground.visibility = View.GONE
                }
            }
        }
        return listener
    }

    fun stopAnimation() {
        animatorSet?.cancel()
    }

    @SuppressLint("NewApi")
    fun animateDropdownSlide(view: View?, heightToMove: Int?, slideLength: Long, down: Boolean) {
        if (view == null || heightToMove == null) return
        val moveDropdownMenu = if (down) {
            ObjectAnimator.ofFloat(view, "translationY", -heightToMove.toFloat(), 0f)
        } else {
            ObjectAnimator.ofFloat(view, "translationY", 0f, -heightToMove.toFloat())
        }

        animatorSet = AnimatorSet()
        animatorSet?.interpolator = if (down) AccelerateInterpolator() else DecelerateInterpolator()

        animatorSet?.duration = slideLength
        animatorSet?.interpolator = FastOutSlowInInterpolator()

        animatorSet?.play(moveDropdownMenu)
        view.visibility = View.VISIBLE
        animatorSet?.start()
    }
}
