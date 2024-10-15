package com.az.elib.presentation.util

import android.app.Activity
import android.os.Build
import androidx.core.view.WindowInsetsCompat


class SystemBarsHeight (private val activity : Activity){
    fun getActionBarSize(): Int {
        val styledAttributes = activity.theme.obtainStyledAttributes(
            intArrayOf(android.R.attr.actionBarSize)
        )
        val actionBarSize = styledAttributes.getDimension(0, 0f).toInt()
        styledAttributes.recycle()
        return actionBarSize
    }
    fun getStatusBarHeight(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity.window.decorView.rootWindowInsets?.getInsetsIgnoringVisibility(
                WindowInsetsCompat.Type.statusBars()
            )?.top
                ?: 0
        } else {
            val rectangle = android.graphics.Rect()
            activity.window.decorView.getWindowVisibleDisplayFrame(rectangle)
            rectangle.top
        }
    }

    fun getSystemBarsHeight() : Int{
        return getActionBarSize() + getStatusBarHeight()
    }
}