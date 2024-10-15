package com.az.elib.presentation.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import com.az.elib.databinding.LoadingBarAppLayoutBinding

class LoadingBarDialog(private val context: Context) {

    private val loadingDialog = Dialog(context)


    init {
        setupLoadingDialog()
    }

    private fun setupLoadingDialog() {
        val binding = LoadingBarAppLayoutBinding.inflate(LayoutInflater.from(context))
        binding.apply {
            circularProgressIndicator.isIndeterminate = true
        }

        loadingDialog.setContentView(binding.root)
        loadingDialog.setCancelable(false)
        loadingDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    fun showLoadingDialog() {
        if (!loadingDialog.isShowing) {
            loadingDialog.show()
        }
    }
    fun hideLoadingDialog() {
        if (loadingDialog.isShowing) {
            loadingDialog.dismiss()
        }
    }

}
