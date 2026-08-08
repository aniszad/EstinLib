package com.az.elib.domain.usecases.auth

import android.util.Log
import com.az.elib.data.local.MySharedPreferences
import com.az.elib.data.repository.RepositoryAuth
import com.az.elib.data.repository.RepositoryFirestoreAuth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.GoogleAuthProvider
import javax.inject.Inject

class SignInWithGoogleUserCase @Inject constructor(
    private val repositoryAuth: RepositoryAuth,
    private val repositoryFirestore: RepositoryFirestoreAuth,
    private val sharedPreferences: MySharedPreferences
) {

    suspend fun invokeSignInWithGoogle(googleSignInAccount: GoogleSignInAccount) : Result<String> {
        return try {
            // 1. Authenticate with Firebase Auth first
            val credentials = GoogleAuthProvider.getCredential(googleSignInAccount.idToken, null)
            val userIdResult = repositoryAuth.signInWithCredentials(credentials)
            val userId = userIdResult.getOrThrow()

            // 2. Check if email is verified
            val emailIsVerified = repositoryAuth.isEmailVerified().getOrNull()
            if (emailIsVerified == null || emailIsVerified == false) {
                repositoryAuth.signOutUser()
                Log.e("sign in", "email not verified")
                throw Exception("Email not verified")
            }

            // 3. Fetch user data (this also verifies if the user record exists)
            val user = repositoryFirestore.getUserData(userId)
            Log.e("save data", "$user")

            sharedPreferences.saveUserLocalData(user)
            Result.success("User signed in successfully")
        } catch (e: Exception) {
            repositoryAuth.signOutUser()
            Log.e("sign in", e.stackTraceToString())
            Result.failure(e)
        }
    }
}