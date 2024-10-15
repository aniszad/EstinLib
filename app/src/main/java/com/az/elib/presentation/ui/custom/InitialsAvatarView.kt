package com.az.elib.presentation.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View


class InitialsAvatarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var initials: String = ""
    private var backgroundColor: Int = Color.GRAY
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
    }
    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG)

    fun setInitialsAndColor(name: String, color: String) {
        initials = name.split(" ")
            .mapNotNull { it.firstOrNull()?.toUpperCase() }
            .take(2)
            .joinToString("")
        Log.e("InitialsAvatarView", "Initials: $initials, $color")
        backgroundColor = Color.parseColor(color)
        circlePaint.color = backgroundColor
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f
        val radius = Math.min(width, height) / 2f

        // Draw circular background
        canvas.drawCircle(centerX, centerY, radius, circlePaint)

        // Draw text
        val yPos = centerY - (textPaint.descent() + textPaint.ascent()) / 2f
        textPaint.textSize = radius * 0.8f // Adjust this value to change text size relative to circle size
        canvas.drawText(initials, centerX, yPos, textPaint)
    }
}
