package com.az.elib.data.datasource

import android.util.Log
import com.az.elib.domain.models.Comment
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class FirebaseFirestoreFeed {

    companion object {
        private const val POSTS_BATCH = 10L
        private const val ORDER_BY_FIELD = "timestamp"
        private const val FIREBASE_PUBLISHED_POSTS_COLLECTION = "PUBLISHED_POSTS"
        private const val FIREBASE_COMMENTS_COLLECTION = "COMMENTS"
        private const val CHANNEL_FIELD = "channel"
    }

    private val db = Firebase.firestore


    suspend fun getNextPostsBatch(lastVisited: DocumentSnapshot?, channel : String): Result<List<DocumentSnapshot>> {
        return try {
            var query = db.collection(FIREBASE_PUBLISHED_POSTS_COLLECTION)
                .whereEqualTo(CHANNEL_FIELD, channel)
                .orderBy(ORDER_BY_FIELD, Query.Direction.DESCENDING)
                .limit(POSTS_BATCH)
            if (lastVisited != null) {
                query = query.startAfter(lastVisited)
            }
            val querySnapshot = query.get().await()

            Log.e("RETRIEVE POSTS", querySnapshot.documents.toString())
            if (querySnapshot.isEmpty) return Result.success(emptyList())
            Log.e("RETRIEVE POSTS", querySnapshot.documents[0].toString())
            Result.success(querySnapshot.documents)
        } catch (e: Exception) {
            Log.e("RETRIEVE POSTS", e.stackTraceToString())
            Result.failure(e)
        }
    }

    suspend fun incrementReacts(postId: String): Result<Boolean> {
        return try {
            // Add reaction to post
            db.collection(FIREBASE_PUBLISHED_POSTS_COLLECTION).document(postId)
                .update("postReactCount", FieldValue.increment(1)).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addUserIdToReactionList(postId: String, userId: String): Result<Boolean> {
        return try {
            // Add user id to post reaction list
            db.collection(FIREBASE_PUBLISHED_POSTS_COLLECTION).document(postId)
                .update("postLikesIds", FieldValue.arrayUnion(userId)).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun decrementReacts(postId: String): Result<Boolean> {
        return try {
            // Add reaction to post
            db.collection(FIREBASE_PUBLISHED_POSTS_COLLECTION).document(postId)
                .update("postReactCount", FieldValue.increment(-1)).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeUserIdFromReactionList(postId: String, userId: String): Result<Boolean> {
        return try {
            db.collection(FIREBASE_PUBLISHED_POSTS_COLLECTION).document(postId)
                .update("postLikesIds", FieldValue.arrayRemove(userId)).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addComment(comment: Comment) {
        db.collection(FIREBASE_PUBLISHED_POSTS_COLLECTION).document(comment.postId)
            .collection(FIREBASE_COMMENTS_COLLECTION)
            .document(comment.id).set(comment).await()
    }

    suspend fun deleteComment(postId: String, commentId: String){
        db.collection(FIREBASE_PUBLISHED_POSTS_COLLECTION).document(postId)
            .collection(FIREBASE_COMMENTS_COLLECTION)
            .document(commentId).delete().await()
    }

    fun createCommentRef(): String {
        return db.collection(FIREBASE_COMMENTS_COLLECTION).document().id
    }

    suspend fun getNextCommentsBatch(
        lastVisitedDocument: DocumentSnapshot?,
        postId: String
    ): Result<List<DocumentSnapshot>> {
        return try {
            val querySnapshot =
                if (lastVisitedDocument != null)
                    db.collection(FIREBASE_PUBLISHED_POSTS_COLLECTION).document(postId)
                        .collection(FIREBASE_COMMENTS_COLLECTION)
                        .limit(POSTS_BATCH)
                        .orderBy(ORDER_BY_FIELD, Query.Direction.DESCENDING)
                        .startAfter(lastVisitedDocument)
                        .get().await()
                else db.collection(FIREBASE_PUBLISHED_POSTS_COLLECTION).document(postId)
                    .collection(FIREBASE_COMMENTS_COLLECTION)
                    .limit(POSTS_BATCH)
                    .orderBy(ORDER_BY_FIELD, Query.Direction.DESCENDING)
                    .get().await()
            Log.e("RETRIEVE COMMENTS", querySnapshot.documents.toString())

            if (querySnapshot.isEmpty) return Result.success(emptyList())
            Log.e("RETRIEVE COMMENTS", querySnapshot.documents[0].toString())
            Result.success(querySnapshot.documents)
        } catch (e: Exception) {
            Log.e("RETRIEVE COMMENTS", e.stackTraceToString())

            Result.failure(e)
        }
    }

    suspend fun deletePost(postId: String) {
        db.collection(FIREBASE_PUBLISHED_POSTS_COLLECTION).document(postId).delete().await()
    }


}