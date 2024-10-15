package com.az.elib.data.local

import com.google.auth.oauth2.GoogleCredentials
import java.io.FileNotFoundException
import java.io.IOException


class FcmMessagingTokenProvider {
    companion object {
        private const val SCOPES = "https://www.googleapis.com/auth/firebase.messaging"
    }
    @Throws(IOException::class)
    fun getAccessToken(): String {
        val googleCredentials = GoogleCredentials
            .fromStream(this@FcmMessagingTokenProvider::class.java.classLoader!!.getResourceAsStream(
                "res/raw/drive_credentials.json"
            )
                ?: throw FileNotFoundException("credentials resource not found")
            )
            .createScoped(listOf(SCOPES))
        googleCredentials.refresh()
        return googleCredentials.accessToken.tokenValue
    }
}