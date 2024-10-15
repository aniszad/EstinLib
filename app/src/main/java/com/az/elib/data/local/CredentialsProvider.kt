package com.az.elib.data.local

import android.content.Context
import com.az.androiddrivepreview.data.interfaces.GdCredentialsProvider
import com.az.elib.R
import java.io.FileNotFoundException
import java.io.InputStream

class CredentialsProvider(private val context: Context) : GdCredentialsProvider {
    override fun getCredentials(): InputStream {
        return context.resources.openRawResource(R.raw.service_account_key)
            ?: throw FileNotFoundException("credentials resource not found")
    }
}