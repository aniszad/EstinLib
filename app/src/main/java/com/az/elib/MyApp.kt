package com.az.elib

import android.app.Application
import android.widget.Toast
import com.az.elib.data.local.CredentialsProvider
import com.az.elib.data.local.TokenManager
import com.google.auth.oauth2.GoogleCredentials
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@HiltAndroidApp
class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()

        val coroutineExceptionHandler = CoroutineExceptionHandler{_, throwable ->
            CoroutineScope(Dispatchers.Main).launch{
                Toast.makeText(this@MyApp, "An error has occurred, check your internet connection", Toast.LENGTH_SHORT).show()
            }
            throwable.printStackTrace()
        }

        try {
            CoroutineScope(Dispatchers.IO+coroutineExceptionHandler).launch {
                val serviceAccount = CredentialsProvider(this@MyApp).getCredentials()
                val googleCredentials = GoogleCredentials.fromStream(serviceAccount)
                val scoped = googleCredentials.createScoped(listOf("https://www.googleapis.com/auth/cloud-platform"))
                TokenManager.accessToken = scoped.refreshAccessToken().tokenValue
            }
        }catch (e: Exception){
            Toast.makeText(this@MyApp, "An error has occurred", Toast.LENGTH_SHORT).show()
        }

    }
}

