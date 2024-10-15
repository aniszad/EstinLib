package com.az.elib.data.repository

import com.az.elib.data.datasource.FirebaseFirestoreUser


class RepositoryUser(private val firebaseFirestoreUser: FirebaseFirestoreUser){
    suspend fun changeUserImage(image: String, userId : String) = firebaseFirestoreUser.changeUserImage(image, userId)
}