package com.az.elib.data.datasource

import com.az.elib.util.Constants
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class FirebaseFirestoreUser {
    private val db = Firebase.firestore

    suspend fun changeUserImage(image: String, userId: String) : Result<String> {
        return try{
            db.collection(Constants.USERS).document(userId).update("image", image).await()
            Result.success(image)
        }catch(e : Exception){
            Result.failure(e)
        }
    }



}