package com.az.elib.domain.usecases.auth

import android.util.Log
import com.az.elib.data.local.MySharedPreferences
import com.az.elib.data.repository.RepositoryAuth
import com.az.elib.data.repository.RepositoryFirestoreAuth
import com.az.elib.util.InputChecker
import javax.inject.Inject

class SignInUserCase @Inject constructor(
    private val inputChecker: InputChecker,
    private val repositoryAuth: RepositoryAuth,
    private val repositoryFirestore: RepositoryFirestoreAuth,
    private val sharedPreferences: MySharedPreferences
) {

    suspend fun invokeSignInWithEmailAndPassword(email: String?, psw: String?) : Result<String> {
        return try {
            val userEmail = inputChecker.extractEmail(email)
            val userPsw = inputChecker.extractPsw(psw)
            val userId = repositoryAuth.signIn(
                email = userEmail,
                password = userPsw
            )
            val emailIsVerified = repositoryAuth.isEmailVerified().getOrNull()
            if (emailIsVerified == null || emailIsVerified == false) {
                repositoryAuth.sendVerificationEmail()
                repositoryAuth.signOutUser()
                throw Exception("Email not verified")
            }
            val user = repositoryFirestore.getUserData(
                userId.getOrNull()!!
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