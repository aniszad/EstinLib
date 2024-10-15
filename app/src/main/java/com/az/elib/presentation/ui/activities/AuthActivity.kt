package com.az.elib.presentation.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.az.elib.R
import com.az.elib.databinding.ActivityAuthBinding
import com.az.elib.presentation.adapters.EstinGalleryRvAdapter
import com.az.elib.presentation.viewmodels.ViewModelAuth
import com.az.elib.presentation.ui.dialogs.ConfirmDialog
import com.az.elib.util.CustomSnackBar
import com.az.elib.util.GoogleSignInHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class AuthActivity : BaseActivity() {
    private lateinit var binding: ActivityAuthBinding
    private val viewModelAuth: ViewModelAuth by viewModels()
    private lateinit var customSnackBar: CustomSnackBar
    private var estinGalleryRvAdapter: EstinGalleryRvAdapter? = null
    private val googleSignInHelper: GoogleSignInHelper by lazy {
        GoogleSignInHelper(this, getString(R.string.server_client_id))
    }
    private val signInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                handleSignInResult(data)
            }
        }
    private val confirmDialog: ConfirmDialog by lazy {
        ConfirmDialog(this@AuthActivity)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPurple))
        setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
        setStatusBarLight(false)
        setEstinImagesGallery()

        // initialize custom snackbar
        customSnackBar = CustomSnackBar(binding.root, this@AuthActivity)

        // setting the functionality of sign in and sign up buttons
        setButtonsFunctionality()

        // setting the year spinner
        setYearSpinner()

        // observing the view model live data
        setViewModelObservers()

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                when (binding.vfAuth.displayedChild) {
                    1 -> {
                        binding.vfAuth.showPrevious()
                    }
                    2 -> {
                        binding.vfAuth.showPrevious()
                        binding.vfAuth.showPrevious()
                    }
                    else -> {
                        finishAffinity()
                    }
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)

    }

    private fun setEstinImagesGallery() {
        estinGalleryRvAdapter = EstinGalleryRvAdapter(
            listOf(
                R.drawable.im_estin_gallery_1,
                R.drawable.im_estin_gallery_2,
                R.drawable.im_estin_gallery_3,
                R.drawable.im_estin_gallery_4
            )
        )
        binding.includeSignInLayout.apply {
            rvEstinGallery.adapter = estinGalleryRvAdapter
        }
        startGallery()
    }

    private fun setYearSpinner() {
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.years_array,
            android.R.layout.simple_spinner_item
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.includeSignUpLayout.spinnerYear.adapter = adapter


        binding.includeSignUpLayout.spinnerYear.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    viewModelAuth.setYear(adapter.getItem(position).toString())
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
    }

    private fun setViewModelObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModelAuth.signInResult.collect { result ->
                        hideLoadingButton()
                        result?.let {
                            if (it.isSuccess) {
                                startActivity(Intent(this@AuthActivity, HomeActivity::class.java))
                                finishAffinity()
                            } else {
                                customSnackBar.launchSnackBar(
                                    message = "Sign in failed",
                                    error = true
                                )
                            }
                            viewModelAuth.clearSignInResult() // Clear the result after handling
                        }
                    }
                }
                launch {
                    viewModelAuth.signInWithGoogleResult.collect { result ->
                        Log.e("google sign in ", "result: $result")
                        result?.let {
                            if (it.isSuccess) {
                                startActivity(Intent(this@AuthActivity, HomeActivity::class.java))
                                finishAffinity()
                            } else {
                                customSnackBar.launchSnackBar(
                                    message = "Sign in failed",
                                    error = true
                                )
                            }
                            viewModelAuth.clearSignInWithGoogleResult() // Clear the result after handling
                        }
                    }
                }
                launch {
                    viewModelAuth.verificationEmailResult.collect { result ->
                        result?.let {
                            if (it.isSuccess) {
                                switchToConfirmVerification()
                            } else {
                                customSnackBar.launchSnackBar(
                                    message = "Verification email failed to send",
                                    error = true
                                )
                            }
                            viewModelAuth.clearVerificationEmailResult() // Clear the result after handling
                        }
                    }
                }
                launch {
                    viewModelAuth.signUpResult.collect { result ->
                        result?.let {
                            if (it.isSuccess) {
                                sendVerificationEmail()
                            } else {
                                customSnackBar.launchSnackBar(
                                    message = result.exceptionOrNull()?.message ?: "Sign up has failed",
                                    error = true
                                )
                            }
                            viewModelAuth.clearSignUpResult() // Clear the result after handling
                        }
                    }
                }
                launch {
                    viewModelAuth.sendResetPswEmail.collect { result ->
                        result?.let {
                            if (it.isSuccess) {
                                customSnackBar.launchSnackBar(
                                    message = "A password reset email was sent to ${it.getOrNull() ?: "your email address"}.",
                                    error = false
                                )
                            } else {
                                customSnackBar.launchSnackBar(
                                    message = "Failed to send the password reset email.",
                                    error = true
                                )
                            }
                            viewModelAuth.clearSendResetPswEmail() // Clear the result after handling
                        }
                    }
                }
            }
        }
    }
    private fun sendVerificationEmail() {
        viewModelAuth.sendVerificationEmail()
    }

    private fun sendResetPasswordEmail(email: String) {
        viewModelAuth.sendResetPasswordEmail(email)
    }

    private fun switchToConfirmVerification() {
        binding.apply {
            includeSignUpLayout.apply {
                etLastNameSignUp.text.clear()
                etFirstNameSignUp.text.clear()
                etEmailSignUp.text.clear()
                etPswSignUp.text.clear()
            }
        }
        binding.vfAuth.showNext()
    }

    private fun setButtonsFunctionality() {
        setViewFlipper()
        binding.apply {
            // Setting Sign In UI functionality
            includeSignInLayout.apply {
                btnSignInWithGoogle.setOnClickListener {
                    signInWithGoogle()
                }
                btnSignInWithEmailAndPsw.setOnClickListener {
                    showLoadingButton()
                    viewModelAuth.signIn()
                }
                btnSwitchToSignUp.setOnClickListener {
                    vfAuth.showNext()
                }
                etEmail.addTextChangedListener { editable ->
                    viewModelAuth.setSignInEmail(editable.toString())
                }
                etPsw.addTextChangedListener { editable ->
                    viewModelAuth.setSignInPassword(editable.toString())
                }
                tvForgotPsw.setOnClickListener {
                    if (etEmail.text.isNotBlank()) {
                        confirmDialog.showConfirmDialog(
                            "Proceed to send an email to the following address: ${etEmail.text}?",
                            R.drawable.ic_mail,
                        ){sendResetPasswordEmail(etEmail.text.toString())}
                    } else {
                        customSnackBar.launchSnackBar("Please enter a valid email", true)
                    }
                }
            }

            // Setting Sign Up UI functionality
            includeSignUpLayout.apply {
                etFirstNameSignUp.addTextChangedListener { editable ->
                    viewModelAuth.setUserFirstName(editable.toString())
                }
                etLastNameSignUp.addTextChangedListener { editable ->
                    viewModelAuth.setUserLastName(editable.toString())
                }
                etEmailSignUp.addTextChangedListener { editable ->
                    viewModelAuth.setUserEmail(editable.toString())
                }
                etPswSignUp.addTextChangedListener { editable ->
                    viewModelAuth.setUserPassword(editable.toString())
                }
                btnSubmitSignUp.setOnClickListener {
                    viewModelAuth.signUp()
                }
                btnSwitchToSignIn.setOnClickListener {
                    vfAuth.showPrevious()
                }
            }

            includeSignUpFinalLayout.apply {
                btnConfirm.setOnClickListener {
                    vfAuth.displayedChild = 0
                }
            }
        }
    }

    private fun setViewFlipper() {
        val inAnimation = AnimationUtils.loadAnimation(
            this,
            androidx.appcompat.R.anim.abc_grow_fade_in_from_bottom
        )
        val outAnimation = AnimationUtils.loadAnimation(
            this,
            androidx.appcompat.R.anim.abc_shrink_fade_out_from_bottom
        )
        binding.apply {
            vfAuth.inAnimation = inAnimation
            vfAuth.outAnimation = outAnimation
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInHelper.getSignInIntent()
        signInLauncher.launch(signInIntent)

    }

    private fun handleSignInResult(data: Intent?) {
        val googleSignInAccount = googleSignInHelper.handleSignInResult(data)
        Log.e("google sign in", "$googleSignInAccount")
        if (googleSignInAccount != null) {
            try {
                viewModelAuth.signInWithGoogle(
                    googleSignInAccount ?: throw Exception("Google sign in failed")
                )
                googleSignInHelper.signOut()
            } catch (e: Exception) {
                customSnackBar.launchSnackBar("Google sign in failed", error = true)
            }
        } else {
            customSnackBar.launchSnackBar("Google sign in failed", error = true)
        }
    }

    private fun showLoadingButton() {
        binding.includeSignInLayout.apply {
            progressBar.visibility = View.VISIBLE
            btnSignInWithEmailAndPsw.text = null
        }
    }

    private fun hideLoadingButton() {
        binding.includeSignInLayout.apply {
            progressBar.visibility = View.GONE
            btnSignInWithEmailAndPsw.text = "Sign in"
        }
    }
    fun startGallery() {
        CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                binding.includeSignInLayout.rvEstinGallery.apply{
                   currentItem = (currentItem + 1) % (estinGalleryRvAdapter?.getSize()?:4)
                }

                delay(6000)
            }
        }
    }
}