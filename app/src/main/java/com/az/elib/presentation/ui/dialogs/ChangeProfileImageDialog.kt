package com.az.elib.presentation.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import com.az.elib.databinding.ChangeProfileImageLayoutBinding
import com.az.elib.presentation.adapters.ImagePagerAdapter

class ChangeProfileImageDialog(private val context: Context) {

    private val changeImDialog = Dialog(context)


    fun setupLoadingDialog(onConfirm: (image: String) -> Unit = {}) {
        val binding = ChangeProfileImageLayoutBinding.inflate(LayoutInflater.from(context))

        val adapter = ImagePagerAdapter(
            listOf(

            )
        )
        binding.viewPagerProfileIm.adapter = adapter
        binding.viewPagerProfileIm.offscreenPageLimit = 3 // Keep 3 pages on each side in memory
        binding.viewPagerProfileIm.setPageTransformer(ImagePagerAdapter.CoverPageTransformer())

        // Optional: Add some padding to the ViewPager2 to show part of adjacent pages
        val padding = 8
        binding.viewPagerProfileIm.setPadding(padding, 0, padding, 0)
        binding.viewPagerProfileIm.clipToPadding = false

        changeImDialog.setContentView(binding.root)
        changeImDialog.setCancelable(false)
        changeImDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        binding.viewPagerProfileIm.isUserInputEnabled = true
//        binding.btnConfirm.setOnClickListener {
//            val selectedImage = when (adapter.images[binding.viewPagerProfileIm.currentItem]) {
//                R.drawable.im_av_1 -> "im_av_1"
//                R.drawable.im_av_2 -> "im_av_2"
//                R.drawable.im_av_3 -> "im_av_3"
//                R.drawable.im_av_4 -> "im_av_4"
//                R.drawable.im_av_5 -> "im_av_5"
//                R.drawable.im_av_6 -> "im_av_6"
//                R.drawable.im_av_7 -> "im_av_7"
//                R.drawable.im_av_8 -> "im_av_8"
//                else -> "im_av_1"
//            }
//            onConfirm.invoke(selectedImage)
//            changeImDialog.dismiss()
//        }
        binding.btnCancel.setOnClickListener {
            changeImDialog.dismiss()
        }
        binding.btnChangeIm.setOnClickListener {
            val currentItem = binding.viewPagerProfileIm.currentItem
            val nextItem = if(currentItem == adapter.itemCount - 1) 0 else currentItem + 1
            binding.viewPagerProfileIm.currentItem = nextItem
        }
    }

    fun show() {
        if (!changeImDialog.isShowing) {
            changeImDialog.show()
        }
    }

    fun hide() {
        if (changeImDialog.isShowing) {
            changeImDialog.dismiss()
        }
    }

}
