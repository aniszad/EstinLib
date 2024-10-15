package com.az.elib.data.repository

import android.util.Log
import com.az.elib.data.datasource.FirebaseFirestoreAuth
import com.az.elib.domain.models.UserLocalData
import javax.inject.Inject

class RepositoryFirestoreAuth @Inject constructor(private val firebaseFirestoreAuth: FirebaseFirestoreAuth) {
    suspend fun checkUsernameDuplicate(email: String): Boolean {
        return try {
            firebaseFirestoreAuth.checkUsernameDuplicate(email)
        } catch (e: Exception) {
            Log.e("Check username duplicate", e.stackTraceToString())
            true
        }
    }

    suspend fun createUser(
        id: String,
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        userYear : String,
        image : String
    ): Result<String> {
        return try {
            firebaseFirestoreAuth.createUser(
                id = id,
                firstName = firstName,
                lastName = lastName,
                email=email,
                password=password,
                userYear = userYear,
                image = image
            )
        } catch (e: Exception) {
            Log.e("Sign up", e.stackTraceToString())
            return Result.failure(e)
        }
    }

    fun deleteUser(userId: String) {

    }

    suspend fun getUserData(userId: String): UserLocalData {
        return firebaseFirestoreAuth.getUserData(userId)
    }

    suspend fun userExists(email: String): Result<Boolean> {
        return firebaseFirestoreAuth.userExists(email)
    }

}