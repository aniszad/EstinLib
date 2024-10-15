package com.az.elib.presentation.util

import android.animation.ValueAnimator
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewSlideAnimator {
    private fun slideView(view: View, start: Int, end: Int, duration: Long = 300): ValueAnimator {
        val animator = ValueAnimator.ofInt(start, end)
        animator.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Int
            val layoutParams = view.layoutParams
            layoutParams.height = value
            view.layoutParams = layoutParams
        }
        animator.duration = duration
        return animator
    }

    fun expandView(recyclerView: RecyclerView) {
        recyclerView.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val targetHeight = recyclerView.measuredHeight

        recyclerView.layoutParams.height = 0
        recyclerView.visibility = View.VISIBLE

        slideView(recyclerView, 0, targetHeight+80).start()
    }

    fun collapseView(recyclerView: RecyclerView) {
        val initialHeight = recyclerView.measuredHeight

        val animator = slideView(recyclerView, initialHeight, 0)
        animator.addListener(object : android.animation.AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                recyclerView.visibility = View.GONE
            }
        })
        animator.start()
    }
}