package com.az.elib.data.datasource

import android.net.Uri
import android.util.Log
import com.az.elib.domain.models.Post
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await

class FirebaseFirestoreCreatePost {

    private val db = Firebase.firestore
    private val storage = Firebase.storage.reference


    suspend fun createPost(post: Post): Result<Post> {
        return try {
            val docRef = db.collection("PUBLISHED_POSTS").document(post.id!!)
            docRef.set(post).await()
            Result.success(post)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadAttach(value: MutableMap.MutableEntry<String, Uri>, userId: String, postId: String): Result<String> {
        return try {
            Log.e("create_post", "uploadAttach: ${value}//${userId}//${postId}")
            val ref = storage.child("POSTS_ATTACHMENTS").child(userId).child(postId).child(value.key)
            ref.putFile(value.value).await()
            Result.success(ref.downloadUrl.await().toString())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun deleteAttach(postImageAttachment: String) {
        storage.child(postImageAttachment).delete()
    }

    fun getFirebaseDocRef(): Result<String> {
        return try  {
            Result.success(db.collection("PUBLISHED_POSTS").document().id)
        }catch (e : Exception){
            Result.failure(e)
        }
    }

}