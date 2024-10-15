package com.az.elib.data.datasource

import android.util.Log
import com.az.elib.domain.models.User
import com.az.elib.domain.models.UserLocalData
import com.az.elib.util.ModelMapper
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class FirebaseFirestoreAuth {
    private val db = Firebase.firestore
    private val modelMapper = ModelMapper()
    suspend fun checkUsernameDuplicate(email: String) : Boolean {
        return  try {
            val result = db.collection("USERS").whereEqualTo("email", email).get().await()
             !result.isEmpty
        }catch (e: Exception){
            true
        }
    }

    suspend fun createUser(id : String, firstName: String, lastName: String, email: String, password: String, userYear:String, image: String): Result<String> {
        return try {
            db.collection("USERS").document(id).set(
                User(
                    id = id,
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                    password = password,
                    suspended = false,
                    year = userYear,
                    image = image
                )
            ).await()
            Result.success("User created")
        } catch (e: Exception) {
            Log.e("firestore error", e.stackTraceToString())
            Result.failure(e)
        }
    }

    suspend fun getUserData(userId: String): UserLocalData {
        Log.e("sign in", "userId: $userId")
        val firebaseUser = db.collection("USERS").whereEqualTo("id", userId).get().await()
        Log.e("sign in", "userId: ${firebaseUser.documents.toString()}")

        val user = modelMapper.mapUserToUserEntity(firebaseUser.documents[0].data)
        return user ?: throw Exception("User not found")
    }

    suspend fun userExists(email: String): Result<Boolean> {
        return try {
            val result = db.collection("USERS").whereEqualTo("email", email).get().await()
            if (result.isEmpty) {
                Result.success(false)
            } else {
                Result.success(true)
            }
        } catch (e: Exception) {
            Log.e("User exists", e.stackTraceToString())
            Result.failure(e)
        }
    }
}