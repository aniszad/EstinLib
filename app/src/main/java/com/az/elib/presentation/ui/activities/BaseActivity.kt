package com.az.elib.presentation.ui.activities

import android.app.Dialog
import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.az.elib.databinding.WarningDialogLayoutBinding


open class BaseActivity : AppCompatActivity() {
    private lateinit var mWarningDialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    fun setStatusBarLight(isLight : Boolean){
        val decorView = window.decorView
        val wic = WindowInsetsControllerCompat(window, decorView)
        wic.isAppearanceLightStatusBars = isLight
    }
    fun setSystemBarsColors(color: Int){
        setStatusBarColor(color)
        setNavigationBarColor(color)
    }
    fun setStatusBarColor(color: Int){
        window.statusBarColor = color
    }
    fun setNavigationBarColor(color: Int){
        window.navigationBarColor = color
    }
    fun customizeSystemBars(rootViewId : Int, color:Int){
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(rootViewId)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, -10, systemBars.right, 0)
            insets
        }

    }

    fun showWarningDialog(content : String, dialogIcon:Int,onConfirmed : () -> Unit) {
        mWarningDialog = Dialog(this)
        val bindingDialog = WarningDialogLayoutBinding.inflate(layoutInflater)
        mWarningDialog.setContentView(bindingDialog.root)
        mWarningDialog.setCancelable(true)
        mWarningDialog.setCanceledOnTouchOutside(true)
        mWarningDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        bindingDialog.tvContent.text = content
        bindingDialog.imWarningExclamation.setImageDrawable(ContextCompat.getDrawable(
            this@BaseActivity, dialogIcon
        ))
        bindingDialog.btnConfirm.setOnClickListener {
            onConfirmed.invoke()
            hideWarningDialog()
        }
        bindingDialog.btnCancel.setOnClickListener {
            hideWarningDialog()
        }

        if (window != null && window.hasFeature(Window.FEATURE_NO_TITLE)) {
            mWarningDialog.show()
        }
    }
    fun hideWarningDialog() {
        mWarningDialog.dismiss()
    }
}