package com.az.elib.data.datasource

import android.util.Log
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class FirebaseAuth {
    private val auth = Firebase.auth


    suspend fun signIn(email: String, password: String): Result<String> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser: FirebaseUser? = authResult.user
            if (firebaseUser != null) {
                Result.success(firebaseUser.uid)
            } else {
                Log.e("Sign in", "User not found")
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            Log.e("Sign in", e.stackTraceToString())
            Result.failure(e)
        }
    }

    suspend fun signInWithCredentials(credential: AuthCredential): Result<String> {
        return try {
            val authResult = auth.signInWithCredential(credential).await()
            Log.e("Google sign in", authResult.toString())

            val firebaseUser: FirebaseUser? = authResult.user
            if (firebaseUser != null) {
                Result.success(firebaseUser.uid)
            } else {
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signUp(email: String, password: String): Result<String> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser: FirebaseUser? = authResult.user
            if (firebaseUser != null) {
                Result.success(firebaseUser.uid)
            } else {
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            Log.e("Sign up", e.stackTraceToString())
            Result.failure(e)
        }
    }

    suspend fun deleteUser(): Boolean {
        return try {
            auth.currentUser?.delete()?.await()
            true
        } catch (e: Exception) {
            Log.e("Delete user", e.stackTraceToString())
            false
        }
    }

    suspend fun sendVerificationEmail(): Result<Boolean> {
        return try {
            auth.currentUser?.sendEmailVerification()?.await()
            Result.success(true)
        } catch (e: Exception) {
            Log.e("Send verification email", e.stackTraceToString())
            Result.failure(e)
        }
    }

    fun isEmailVerified(): Result<Boolean> {
        return Result.success(
            auth.currentUser?.isEmailVerified ?: throw Exception("User not found")
        )
    }

    fun signOutUser() : Boolean {
        return try {
            auth.signOut()
            true
        }catch (e : Exception){
            false
        }
    }

    suspend fun userExists(email: String): Result<Boolean> {
        val result = auth.fetchSignInMethodsForEmail(email).await()
        val userExists = result.signInMethods?.isNotEmpty() ?: false
        return if (userExists) {
            Result.success(true)
        } else {
            Result.failure(Exception("User not found"))
        }
    }

    fun isUserSignedIn(): Result<Boolean> {
        return try {
             if (auth.currentUser != null && auth.currentUser?.isEmailVerified != null) {
                if (auth.currentUser?.isEmailVerified!!){
                    Result.success(true)
                }else{
                    Result.success(false)
                }
            } else {
                Result.success(false)
            }
        }catch (e : Exception){
            Result.failure(e)
        }
    }

    suspend fun sendChangePasswordEmail(): Boolean {
        return try {
            val userEmail = auth.currentUser?.email ?:""
            auth.sendPasswordResetEmail(userEmail).await()
            true
        } catch (e: Exception) {
            Log.e("Send change password email", e.stackTraceToString())
            false
        }
    }

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    suspend fun sendResetPswEmail(email: String): Result<String> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(email)
        }catch (e: Exception){
            Result.failure(e)
        }
    }


}