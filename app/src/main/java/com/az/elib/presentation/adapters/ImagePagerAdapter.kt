package com.az.elib.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.az.elib.databinding.ProfileImageItemLayoutBinding


class ImagePagerAdapter(private val images: List<Int>) :
    RecyclerView.Adapter<ImagePagerAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(
            ProfileImageItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.imageView.setImageResource(images[position])
    }

    override fun getItemCount(): Int = images.size

    class ImageViewHolder(binding: ProfileImageItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val imageView: ImageView = binding.imUser
    }

    class CoverPageTransformer : ViewPager2.PageTransformer {
        private val MIN_SCALE = 0.85f
        private val MAX_ROTATION = 15f

        override fun transformPage(page: View, position: Float) {
            val pageWidth = page.width

            when {
                position < -1 -> { // Page is far off-screen to the left
                    page.alpha = 0f
                }
                position <= 1 -> { // Page is visible or entering
                    // Scale the page down (between MIN_SCALE and 1)
                    val scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position))
                    val vertMargin = pageWidth * (1 - scaleFactor) / 2
                    val horzMargin = pageWidth * position

                    // Center vertically
                    page.translationY = vertMargin

                    // Move page horizontally
                    page.translationX = if (position < 0) {
                        horzMargin - vertMargin / 2
                    } else {
                        horzMargin + vertMargin / 2
                    }

                    // Scale the page down
                    page.scaleX = scaleFactor
                    page.scaleY = scaleFactor

                    // Rotate the page
                    page.rotationY = position * -MAX_ROTATION

                    // Keep alpha at 1 for less transparency
                    page.alpha = 1f
                }
                else -> { // Page is far off-screen to the right
                    page.alpha = 0f
                }
            }
        }
    }


}

