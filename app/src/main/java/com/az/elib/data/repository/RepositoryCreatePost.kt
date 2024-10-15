package com.az.elib.data.repository

import android.net.Uri
import com.az.elib.data.datasource.FirebaseFirestoreCreatePost
import com.az.elib.domain.models.Post
import javax.inject.Inject

class RepositoryCreatePost @Inject constructor(private val firebaseFirestoreCreatePost: FirebaseFirestoreCreatePost) {
    suspend fun createPost(post: Post): Result<Post>{
        return firebaseFirestoreCreatePost.createPost(post)
    }

    suspend fun uploadAttach(value: MutableMap.MutableEntry<String, Uri>, userId: String, postId: String) : Result<String> {
        return firebaseFirestoreCreatePost.uploadAttach(value, userId, postId)
    }

    fun deleteAttach(postImageAttachment: String) {
        firebaseFirestoreCreatePost.deleteAttach(postImageAttachment)
    }

    fun getFirebaseDocRef(): Result<String> {
        return firebaseFirestoreCreatePost.getFirebaseDocRef()
    }

}