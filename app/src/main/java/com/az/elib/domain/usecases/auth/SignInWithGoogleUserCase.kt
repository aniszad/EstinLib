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
            val userExists = repositoryFirestore.userExists(googleSignInAccount.email ?: throw Exception("Couldn't sign in"))
            if (userExists.isFailure) throw Exception(userExists.exceptionOrNull()?.message ?: "User not found")
            val credentials = GoogleAuthProvider.getCredential(googleSignInAccount.idToken, null)
            val userId = repositoryAuth.signInWithCredentials(credentials)
            val emailIsVerified = repositoryAuth.isEmailVerified().getOrNull()
            if (emailIsVerified == null || emailIsVerified == false) {
                repositoryAuth.signOutUser()
                Log.e("sign in", "email not verified")
                throw Exception("Email not verified")
            }
            val user = repositoryFirestore.getUserData(
                userId.getOrNull() ?: throw Exception("Couldn't sign in")
            )
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