package com.az.elib.data.repository

import com.az.elib.data.datasource.FirebaseAuth
import com.google.firebase.auth.AuthCredential
import javax.inject.Inject

class RepositoryAuth @Inject constructor(private val firebaseAuth: FirebaseAuth) {
    suspend fun signIn(email: String, password: String) : Result<String> {
        return firebaseAuth.signIn(email, password)
    }
    suspend fun signInWithCredentials(credentials: AuthCredential): Result<String> {
        return firebaseAuth.signInWithCredentials(credentials)
    }

    suspend fun signUp(email: String, password: String): Result<String> {
        return firebaseAuth.signUp(email, password)
    }

    suspend fun deleteUser() : Boolean {
        return firebaseAuth.deleteUser()
    }

    suspend fun sendVerificationEmail() : Result<Boolean> {
      return firebaseAuth.sendVerificationEmail()
    }

    fun isEmailVerified(): Result<Boolean> {
        return firebaseAuth.isEmailVerified()
    }

    fun signOutUser() : Boolean {
        return firebaseAuth.signOutUser()
    }

    suspend fun userExists(email: String): Result<Boolean> {
        return firebaseAuth.userExists(email)
    }

    fun isUserSignedIn(): Result<Boolean> {
        return firebaseAuth.isUserSignedIn()
    }

    suspend fun sendChangePasswordEmail(): Boolean {
        return firebaseAuth.sendChangePasswordEmail()
    }

    fun getCurrentUserId(): String? {
        return firebaseAuth.getCurrentUserId()
    }

    suspend fun sendResetPasswordEmail(email: String): Result<String> {
        return firebaseAuth.sendResetPswEmail(email)
    }

}