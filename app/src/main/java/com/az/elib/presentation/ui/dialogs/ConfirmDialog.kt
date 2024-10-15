package com.az.elib.presentation.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import com.az.elib.databinding.WarningDialogLayoutBinding


class ConfirmDialog(private val context: Context){
    private lateinit var confirmDialog: Dialog


    fun showConfirmDialog(content : String, dialogIcon:Int,onConfirmed : () -> Unit) {
        confirmDialog = Dialog(context)
        val bindingDialog = WarningDialogLayoutBinding.inflate(LayoutInflater.from(context))
        confirmDialog.setContentView(bindingDialog.root)
        confirmDialog.setCancelable(true)
        confirmDialog.setCanceledOnTouchOutside(true)
        confirmDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        bindingDialog.tvContent.text = content
        bindingDialog.imWarningExclamation.setImageDrawable(
            ContextCompat.getDrawable(
            context, dialogIcon
        ))
        bindingDialog.btnConfirm.setOnClickListener {
            onConfirmed.invoke()
            confirmDialog.dismiss()
        }
        bindingDialog.btnCancel.setOnClickListener {
            confirmDialog.dismiss()
        }
        confirmDialog.show()
    }



}