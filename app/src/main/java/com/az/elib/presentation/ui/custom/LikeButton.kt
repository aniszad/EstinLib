package com.az.elib.presentation.ui.custom

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton
import com.az.elib.R

class LikeButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageButton(context, attrs, defStyleAttr) {

    private var isLiked = false
    private val animationDuration = 300L // milliseconds

    var onLikeChanged: ((Boolean) -> Unit)? = null

    init {
        setImageResource(R.drawable.icon_heart_outline)
        setOnClickListener { toggle() }
    }

    fun setLiked(liked: Boolean, animate: Boolean = false) {
        if (isLiked != liked) {
            isLiked = liked
            updateDrawable(animate)
        }
    }

    fun reverseLiked() {
        // when the btn is clicked through the image
        toggle()

    }

    private fun toggle() {
        onLikeChanged?.invoke(isLiked)
        isLiked = !isLiked
        updateDrawable(true)

    }

    private fun updateDrawable(animate: Boolean) {
        val newDrawableRes = if (isLiked) R.drawable.icon_heart_filled else R.drawable.icon_heart_outline
        val tint = ColorStateList.valueOf(context.getColor(if (isLiked) R.color.colorPurple else R.color.white))
        if (animate) {
            animate()
                .scaleX(0f)
                .scaleY(0f)
                .setDuration(animationDuration / 2)
                .withEndAction {
                    setImageResource(newDrawableRes)
                    imageTintList = tint
                    animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(animationDuration / 2)
                        .start()
                }
                .start()
        } else {
            setImageResource(newDrawableRes)
        }
    }


}