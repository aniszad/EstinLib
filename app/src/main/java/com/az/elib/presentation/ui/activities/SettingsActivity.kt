package com.az.elib.presentation.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.az.elib.R
import com.az.elib.databinding.ActivitySettingsBinding
import com.az.elib.presentation.viewmodels.ViewModelSettings
import com.az.elib.util.CustomSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale


@AndroidEntryPoint
class SettingsActivity : BaseActivity() {

    private lateinit var binding : ActivitySettingsBinding

    private val viewModelSettings : ViewModelSettings by viewModels()
    private val customSnackBar : CustomSnackBar by lazy {
        CustomSnackBar(binding.root, this@SettingsActivity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setNavigationBarColor(ContextCompat.getColor(this@SettingsActivity, R.color.colorPrimaryDark))
        setStatusBarLight(false)
        setUiFunc()
        setViewModelObservers()
        getUserInfo()
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun getUserInfo() {
        viewModelSettings.getUserInfo()
    }

    private fun setViewModelObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModelSettings.getNotificationsAllowed()
                }
                launch {
                    viewModelSettings.sendPasswordChangeEmailResult.collect { emailSent ->
                        if (emailSent != null){
                            if(emailSent) {
                            customSnackBar.launchSnackBar("Email sent successfully", false)
                        } else {
                            customSnackBar.launchSnackBar("Failed to send email", true)
                        }
                    }
                }
                }
                launch {
                    viewModelSettings.notificationsAllowed.collect { allowed ->
                        binding.apply {
                            switchNotificationsAllowed.isChecked = allowed
                            // setting the switch on listener after updating the state
                            switchNotificationsAllowed.setOnCheckedChangeListener { _, isChecked ->
                                viewModelSettings.updateNotificationAllowed(isChecked)
                            }
                        }
                    }
                }
                launch {
                    viewModelSettings.signOutUserResult.collect { signedOut ->
                        if (signedOut != null){
                            if (signedOut) {
                                customSnackBar.launchSnackBar("Signed out successfully", false)
                                val intent = Intent(this@SettingsActivity, AuthActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                                finishAffinity()
                            } else {
                                customSnackBar.launchSnackBar("Failed to sign out", true)
                            }
                        }
                    }
                }
                launch {
                    viewModelSettings.fullName.collect { fullName ->
                        binding.tvFullName.text = fullName.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                        }
                    }
                }
                launch {
                    viewModelSettings.userEmail.collect { email ->
                        binding.tvEmail.text = email?.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                        }
                    }
                }
                launch {
                    viewModelSettings.userYear.collect { year ->
                        binding.tvYear.text = year
                    }
                }
                launch {
                    viewModelSettings.userImage.collect{    backgColor ->
                        if (backgColor?.isNotBlank() == true) {
                            Log.e("InitialsAvatarView", "backg : $backgColor")
                            binding.imUser.setInitialsAndColor(viewModelSettings.fullName.value, backgColor ?: "#000000")
                        }
                    }
                }
                launch {
                    viewModelSettings.changeImageResult.collect{ imageChangeResult ->
                        if (imageChangeResult != null){
                            if (imageChangeResult.isSuccess){
                                customSnackBar.launchSnackBar("Image changed successfully", false)
                            } else {
                                customSnackBar.launchSnackBar("Failed to change image", true)
                            }
                        }
                    }
                }
            }
        }
    }
    private fun setUiFunc() {
        binding.apply {
            imUser.setOnClickListener {
                showWarningDialog("Custom images coming soon!", R.drawable.icon_camera) {}
//                val changeProfileImageDialog = ChangeProfileImageDialog(this@SettingsActivity)
//                changeProfileImageDialog.setupLoadingDialog { image ->
//                    try {
//                        viewModelSettings.changeUserImage(image)
//                    }catch (e: Exception){
//                        customSnackBar.launchSnackBar("Failed to change image", true)
//                    }
//                }
//                changeProfileImageDialog.show()
            }
            llChangePassword.setOnClickListener {
                showWarningDialog("Are you sure you want to change your password?", R.drawable.icon_key) {
                    viewModelSettings.sendChangePasswordEmail()
                }
            }
            llSignOut.setOnClickListener {
                showWarningDialog("Are you sure you want to sign out?", R.drawable.icon_logout){viewModelSettings.signOutUser()}
            }
            toolbarSettings.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }
}