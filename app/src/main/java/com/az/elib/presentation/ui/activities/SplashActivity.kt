package com.az.elib.presentation.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.az.elib.R
import com.az.elib.presentation.viewmodels.ViewModelSplash
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashActivity : BaseActivity() {

    private val viewModelSplash: ViewModelSplash by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // You can set a custom splash screen layout or skip it to use only the theme
        setContentView(R.layout.activity_splash)
        setSystemBarsColors(ContextCompat.getColor(this@SplashActivity, R.color.colorPrimaryDark))
        // Simulate a short delay for the splash screen (optional)
        Handler(Looper.getMainLooper()).postDelayed({
            setObservable()
            checkIfUserSignedIn()
        }, 1000) // 2 seconds delay
    }

    private fun setObservable() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModelSplash.isUserSignedIn.collect { result ->
                        if (result != null) {
                            if (result.isSuccess) {
                                if (result.getOrNull() == true) {
                                    startActivity(
                                        Intent(
                                            this@SplashActivity,
                                            HomeActivity::class.java
                                        )
                                    )
                                    finishAffinity()
                                } else {
                                    startActivity(
                                        Intent(
                                            this@SplashActivity,
                                            AuthActivity::class.java
                                        )
                                    )
                                    finishAffinity()
                                }

                            } else {
                                Toast.makeText(
                                    this@SplashActivity,
                                    "an error has occurred. Check your internet connection",
                                    Toast.LENGTH_SHORT
                                ).show()
                                startActivity(Intent(this@SplashActivity, AuthActivity::class.java))
                                finishAffinity()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun checkIfUserSignedIn() {
        viewModelSplash.checkIfUserSignedIn()
    }
}
