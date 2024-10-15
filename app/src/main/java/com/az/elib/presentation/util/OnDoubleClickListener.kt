package com.az.elib.presentation.util



import android.view.View

class OnDoubleClickListener(
    private val doubleClickTime: Long = 300, // Time in milliseconds
    private val onDoubleClick: (View) -> Unit
) : View.OnClickListener {

    private var lastClickTime: Long = 0

    override fun onClick(v: View?) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime < doubleClickTime) {
            onDoubleClick(v ?: return)
        }
        lastClickTime = currentTime
    }
}

